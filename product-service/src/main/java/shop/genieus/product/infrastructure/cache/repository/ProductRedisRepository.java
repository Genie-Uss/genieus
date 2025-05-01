package shop.genieus.product.infrastructure.cache.repository;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;
import shop.genieus.product.domain.model.ProductView;
import shop.genieus.product.domain.model.vo.ProductStatus;
import shop.genieus.product.domain.model.vo.StockEventStatus;
import shop.genieus.product.domain.model.vo.StockEventType;
import shop.genieus.product.global.exception.ProductException;
import shop.genieus.product.infrastructure.cache.util.ProductLuaScriptProvider;

@Slf4j
@Repository
@RequiredArgsConstructor
public class ProductRedisRepository {

  private final RedisTemplate<String, ProductView> productViewRedisTemplate;
  private final RedisTemplate<String, Long> longRedisTemplate;
  private final StringRedisTemplate stringRedisTemplate;

  @Value("${redis.key.prefix.status}")
  private String STATUS_PREFIX;

  @Value("${redis.key.prefix.meta}")
  private String META_PREFIX;

  @Value("${redis.key.prefix.used}")
  private String USED_PREFIX;

  @Value("${redis.key.prefix.total}")
  private String TOTAL_PREFIX;

  @Value("${redis.key.prefix.dedup}")
  private String DEDUP_KEY_PREFIX;

  @Value("${redis.key.event.id-counter}")
  private String EVENT_ID_COUNTER_KEY;

  @Value("${redis.key.event.processing-queue}")
  private String PROCESSING_QUEUE_KEY;

  @Value("${redis.key.ttl.dedup-hours}")
  private int DEDUP_TTL_HOURS;

  @Value("${redis.key.ttl.meta-hours}")
  private long META_TTL_HOURS;

  public Optional<ProductView> findProductViewById(Long id) {
    return Optional.ofNullable(productViewRedisTemplate.opsForValue().get(META_PREFIX + id));
  }

  public List<ProductView> findProductViewListByIds(List<Long> productIds) {
    if (productIds == null || productIds.isEmpty()) {
      return Collections.emptyList();
    }

    List<String> keys =
        productIds.stream().map(id -> META_PREFIX + id).collect(Collectors.toList());

    return Optional.ofNullable(productViewRedisTemplate.opsForValue().multiGet(keys))
        .orElse(Collections.emptyList());
  }

  public void saveProductView(Long id, ProductView productView) {
    productViewRedisTemplate
        .opsForValue()
        .set(META_PREFIX + id, productView, META_TTL_HOURS, TimeUnit.HOURS);
  }

  public void saveProductViewBatch(Map<Long, ProductView> productViewMap) {
    if (productViewMap == null || productViewMap.isEmpty()) return;

    Map<String, ProductView> keyValueMap =
        productViewMap.entrySet().stream()
            .collect(Collectors.toMap(entry -> META_PREFIX + entry.getKey(), Map.Entry::getValue));

    productViewRedisTemplate.opsForValue().multiSet(keyValueMap);

    keyValueMap
        .keySet()
        .forEach(key -> productViewRedisTemplate.expire(key, META_TTL_HOURS, TimeUnit.HOURS));
  }

  public void setInitialTotalStock(Long id, Long total) {
    longRedisTemplate.opsForValue().setIfAbsent(TOTAL_PREFIX + id, total);
  }

  public void setInitialStatus(Long id, String status) {
    stringRedisTemplate.opsForValue().setIfAbsent(STATUS_PREFIX + id, status);
  }

  public List<String> atomicValidateAndDecreaseStock(Map<Long, Integer> productQuantities) {
    /*
      * TODO: Redis Cluster 호환성 개선
      <p>- EVENT_ID_COUNTER_KEY와 PROCESSING_QUEUE_KEY를 KEYS 목록에 추가
      <p>- Lua 스크립트에서 해시 태그({}) 사용하여 키들이 동일한 슬롯에 배치되도록 수정
      <p>예: 'product:{productId}:events', 'event:{productId}:eventId'
    * */
    if (productQuantities == null || productQuantities.isEmpty()) {
      return Collections.emptyList();
    }

    try {
      ScriptArguments args = createValidateAndDecreaseStockArgs(productQuantities);

      List<String> result =
          stringRedisTemplate.execute(
              ProductLuaScriptProvider.getValidateAndDecreaseScript(),
              args.keys,
              args.args.toArray(new String[0]));

      return result != null ? result : Collections.emptyList();
    } catch (Exception e) {
      String message = extractRedisErrorMessage(e);
      throw new ProductException("재고 처리 실패: " + message, e);
    }
  }

  public String atomicDecreaseUsedProductStock(Map<Long, Integer> productQuantities) {
    try {
      ScriptArguments args = createDecreaseUsedProductStockArgs(productQuantities);

      List<String> result =
          stringRedisTemplate.execute(
              ProductLuaScriptProvider.getDecreaseUsedStockScript(),
              args.keys,
              args.args.toArray(new String[0]));

      return result != null ? result.get(0) : "";
    } catch (Exception e) {
      String message = extractRedisErrorMessage(e);
      throw new ProductException("예약 재고 차감 처리 실패: " + message, e);
    }
  }

  public String atomicRestoreStockWithEvents(
      Map<Long, Integer> productQuantities, Long orderId, Long timestamp, Long todayTimestamp) {

    if (productQuantities == null || productQuantities.isEmpty()) {
      return "복구할 재고가 존재하지 않습니다.";
    }

    String deduplicationKey = generateDedupKey(todayTimestamp);
    String dedupValue = generateDedupValue(orderId, timestamp);
    Boolean isDuplicate = stringRedisTemplate.opsForSet().isMember(deduplicationKey, dedupValue);

    if (Boolean.TRUE.equals(isDuplicate)) {
      return "이미 재고 복구가 처리되어 있습니다.";
    }

    try {
      ScriptArguments args =
          createRestoreStockWithEventsArgs(productQuantities, orderId, timestamp, deduplicationKey);

      List<String> result =
          stringRedisTemplate.execute(
              ProductLuaScriptProvider.getRestoreTotalStockScript(),
              args.keys,
              args.args.toArray(new String[0]));

      return result != null ? result.get(0) : "재고 복구 결과가 존재하지 않습니다.";
    } catch (Exception e) {
      String message = extractRedisErrorMessage(e);
      throw new ProductException("재고 복구 처리 실패: " + message, e);
    }
  }

  public List<String> atomicTotalDecreaseStock(
      Map<Long, Integer> productQuantities, Long timestamp, Long orderId, Long todayTimestamp) {

    if (productQuantities == null || productQuantities.isEmpty()) {
      return Collections.emptyList();
    }

    String dedupKey = generateDedupKey(todayTimestamp);
    String dedupValue = generateDedupValue(orderId, timestamp);
    Boolean isDuplicate = stringRedisTemplate.opsForSet().isMember(dedupKey, dedupValue);

    if (Boolean.TRUE.equals(isDuplicate)) {
      log.info("이미 재고 완료 처리된 주문입니다.");
      return Collections.emptyList();
    }

    try {
      List<String> keys = new ArrayList<>();
      keys.add(dedupKey);
      keys.add(EVENT_ID_COUNTER_KEY);

      // 접두사 추가
      List<String> args = new ArrayList<>();
      args.add(TOTAL_PREFIX);
      args.add(USED_PREFIX);
      args.add(STATUS_PREFIX);
      args.add(PROCESSING_QUEUE_KEY);
      args.add(String.valueOf(TimeUnit.HOURS.toSeconds(DEDUP_TTL_HOURS)));

      // 데이터 순회
      for (Map.Entry<Long, Integer> entry : productQuantities.entrySet()) {
        args.add(String.valueOf(entry.getKey())); // value에 상품ID 추가
        args.add(String.valueOf(orderId));

        if (entry.getValue() <= 0) {
          log.error("[atomicTotalDecreaseStock] 차감 수량 에러. productId: {}", entry.getKey());
          throw new ProductException("차감할 수량은 1이상 이어야합니다.");
        }

        args.add(String.valueOf(entry.getValue())); // 상품수량
        args.add(String.valueOf(timestamp));
      }

      List<String> results =
          stringRedisTemplate.execute(
              ProductLuaScriptProvider.getTotalStockDecreaseScript(),
              keys,
              args.toArray(new String[0]));

      if (!results.isEmpty()) {
        log.info("상품 재고 차감 업데이트 결과, {}", results);
      }

      return results;
    } catch (Exception e) {
      String message = extractRedisErrorMessage(e);
      throw new ProductException("상품 재고 차감 업데이트 에러 " + message);
    }
  }

  private ScriptArguments createValidateAndDecreaseStockArgs(Map<Long, Integer> productQuantities) {
    List<String> keys = new ArrayList<>(productQuantities.size());
    List<String> args = new ArrayList<>(5 + productQuantities.size());
    args.add(STATUS_PREFIX);
    args.add(TOTAL_PREFIX);
    args.add(USED_PREFIX);
    args.add(META_PREFIX);
    args.add(ProductStatus.ON_SALE.name());

    addProductQuantitiesToArgs(productQuantities, keys, args);

    return new ScriptArguments(keys, args);
  }

  private ScriptArguments createDecreaseUsedProductStockArgs(Map<Long, Integer> stockQuantities) {

    List<String> keys = new ArrayList<>(stockQuantities.size());
    List<String> args = new ArrayList<>(4 + stockQuantities.size());

    args.add(USED_PREFIX);
    args.add(STATUS_PREFIX);
    args.add(ProductStatus.SOLD_OUT.name());
    args.add(ProductStatus.ON_SALE.name());

    addProductQuantitiesToArgs(stockQuantities, keys, args);

    return new ScriptArguments(keys, args);
  }

  private ScriptArguments createRestoreStockWithEventsArgs(
      Map<Long, Integer> productQuantities, Long orderId, Long timestamp, String dedupKey) {

    List<String> keys = new ArrayList<>(productQuantities.size());
    keys.add(dedupKey);
    keys.add(EVENT_ID_COUNTER_KEY);
    keys.add(PROCESSING_QUEUE_KEY);

    List<String> args = new ArrayList<>(9 + productQuantities.size());

    args.add(STATUS_PREFIX);
    args.add(TOTAL_PREFIX);

    args.add(ProductStatus.SOLD_OUT.name());
    args.add(ProductStatus.ON_SALE.name());

    args.add(String.valueOf(orderId));
    args.add(String.valueOf(timestamp));

    args.add(StockEventType.INCREASE.name());
    args.add(StockEventStatus.PENDING.name());

    args.add(String.valueOf(TimeUnit.HOURS.toSeconds(DEDUP_TTL_HOURS)));

    addProductQuantitiesToArgs(productQuantities, keys, args);

    return new ScriptArguments(keys, args);
  }

  private void addProductQuantitiesToArgs(
      Map<Long, Integer> productQuantities, List<String> keys, List<String> args) {

    for (Map.Entry<Long, Integer> entry : productQuantities.entrySet()) {
      Long productId = entry.getKey();
      Integer quantity = entry.getValue();

      if (quantity == null || quantity <= 0) {
        throw new IllegalArgumentException("수량은 1 이상이어야 합니다. id=" + productId);
      }

      keys.add(String.valueOf(productId));
      args.add(String.valueOf(quantity));
    }
  }

  private String extractRedisErrorMessage(Throwable e) {
    Throwable root = e;
    while (root.getCause() != null) {
      root = root.getCause();
    }
    return Optional.ofNullable(root.getMessage()).orElse(e.getMessage());
  }

  private String generateDedupKey(Long timestamp) {
    return DEDUP_KEY_PREFIX + timestamp;
  }

  private String generateDedupValue(Long orderId, Long timestamp) {
    return orderId + ":" + timestamp;
  }

  private record ScriptArguments(List<String> keys, List<String> args) {}
}
