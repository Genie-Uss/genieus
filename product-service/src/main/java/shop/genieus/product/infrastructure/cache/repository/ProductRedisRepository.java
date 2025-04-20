package shop.genieus.product.infrastructure.cache.repository;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

  private static final String STATUS_PREFIX = "product:status:";
  private static final String META_PREFIX = "product:meta:";
  private static final String USED_PREFIX = "product:stock:used:";
  private static final String TOTAL_PREFIX = "product:stock:total:";
  private static final String EVENT_ID_COUNTER_KEY = "event_id_counter";
  private static final String PROCESSING_QUEUE_KEY = "product:event:stock:queue";
  private static final String RESTORE_DEDUP_KEY_PREFIX = "dedup:restore:";

  private static final int DEDUP_TTL = 24;
  private static final long META_TTL = 24;

  private final RedisTemplate<String, ProductView> productViewRedisTemplate;
  private final RedisTemplate<String, Long> longRedisTemplate;
  private final StringRedisTemplate stringRedisTemplate;

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
        .set(META_PREFIX + id, productView, META_TTL, TimeUnit.HOURS);
  }

  public void saveProductViewBatch(Map<Long, ProductView> productViewMap) {
    if (productViewMap == null || productViewMap.isEmpty()) return;

    Map<String, ProductView> keyValueMap =
        productViewMap.entrySet().stream()
            .collect(Collectors.toMap(entry -> META_PREFIX + entry.getKey(), Map.Entry::getValue));

    productViewRedisTemplate.opsForValue().multiSet(keyValueMap);

    keyValueMap
        .keySet()
        .forEach(key -> productViewRedisTemplate.expire(key, META_TTL, TimeUnit.HOURS));
  }

  public void setInitialTotalStock(Long id, Long total) {
    longRedisTemplate.opsForValue().setIfAbsent(TOTAL_PREFIX + id, total);
  }

  public void setInitialStatus(Long id, String status) {
    stringRedisTemplate.opsForValue().setIfAbsent(STATUS_PREFIX + id, status);
  }

  public List<String> atomicValidateAndDecreaseStock(Map<Long, Integer> productQuantities) {
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

  public List<String> atomicRestoreStockWithEvents(
      Map<Long, Integer> productQuantities, Long orderId, Long timestamp) {

    if (productQuantities == null || productQuantities.isEmpty()) {
      return Collections.emptyList();
    }

    try {
      ScriptArguments args =
          createRestoreStockWithEventsArgs(productQuantities, orderId, timestamp);

      List<String> result =
          stringRedisTemplate.execute(
              ProductLuaScriptProvider.getRestoreStockWithEventsScript(),
              args.keys,
              args.args.toArray(new String[0]));

      return result != null ? result : Collections.emptyList();
    } catch (Exception e) {
      String message = extractRedisErrorMessage(e);
      throw new ProductException("재고 복구 처리 실패: " + message, e);
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

  private ScriptArguments createRestoreStockWithEventsArgs(
      Map<Long, Integer> productQuantities, Long orderId, Long timestamp) {

    List<String> keys = new ArrayList<>(productQuantities.size());
    List<String> args = new ArrayList<>(13 + productQuantities.size());

    args.add(STATUS_PREFIX);
    args.add(TOTAL_PREFIX);
    args.add(USED_PREFIX);

    args.add(ProductStatus.SOLD_OUT.name());
    args.add(ProductStatus.ON_SALE.name());

    args.add(String.valueOf(orderId));
    args.add(String.valueOf(timestamp));

    args.add(EVENT_ID_COUNTER_KEY);
    args.add(PROCESSING_QUEUE_KEY);
    args.add(StockEventType.INCREASE.name());
    args.add(StockEventStatus.PENDING.name());

    args.add(RESTORE_DEDUP_KEY_PREFIX);
    args.add(String.valueOf(DEDUP_TTL));

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

  private record ScriptArguments(List<String> keys, List<String> args) {}
}
