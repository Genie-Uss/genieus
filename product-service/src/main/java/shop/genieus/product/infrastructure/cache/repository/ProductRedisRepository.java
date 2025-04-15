package shop.genieus.product.infrastructure.cache.repository;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.RedisSystemException;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;
import shop.genieus.product.domain.model.ProductView;
import shop.genieus.product.infrastructure.cache.util.ProductLuaScriptProvider;

@Slf4j
@Repository
@RequiredArgsConstructor
public class ProductRedisRepository {

  private static final String META_PREFIX = "product:meta:";
  private static final String USED_PREFIX = "product:stock:used:";
  private static final String TOTAL_PREFIX = "product:stock:total:";
  private static final long META_TTL = 24;

  private final RedisTemplate<String, ProductView> productViewRedisTemplate;
  private final RedisTemplate<String, Long> longRedisTemplate;
  private final StringRedisTemplate stringRedisTemplate;

  public Optional<ProductView> findProductViewById(Long id) {
    return Optional.ofNullable(productViewRedisTemplate.opsForValue().get(META_PREFIX + id));
  }

  public List<ProductView> findProductViewListByIds(List<Long> productIds) {
    List<String> keys =
        productIds.stream().map(id -> META_PREFIX + id).collect(Collectors.toList());

    if (keys == null || keys.isEmpty()) {
      return Collections.emptyList();
    }

    return productViewRedisTemplate.opsForValue().multiGet(keys);
  }

  public void saveProductView(Long id, ProductView productView) {
    String key = META_PREFIX + id;
    productViewRedisTemplate.opsForValue().set(key, productView, META_TTL, TimeUnit.HOURS);
  }

  public void saveProductViewBatch(Map<Long, ProductView> productViewMap) {
    if (productViewMap == null || productViewMap.isEmpty()) return;

    Map<String, ProductView> keyValueMap = new HashMap<>();
    for (Map.Entry<Long, ProductView> entry : productViewMap.entrySet()) {
      keyValueMap.put(META_PREFIX + entry.getKey(), entry.getValue());
    }

    productViewRedisTemplate.opsForValue().multiSet(keyValueMap);

    for (Long id : productViewMap.keySet()) {
      productViewRedisTemplate.expire(META_PREFIX + id, META_TTL, TimeUnit.HOURS);
    }
  }

  public Long getUsedStock(Long id) {
    String key = USED_PREFIX + id;
    Long value = longRedisTemplate.opsForValue().get(key);
    return value != null ? value : 0L;
  }

  public Long getTotalStock(Long id) {
    String key = TOTAL_PREFIX + id;
    Long value = longRedisTemplate.opsForValue().get(key);
    return value != null ? value : 0L;
  }

  public void setTotalStock(Long id, Long total) {
    longRedisTemplate.opsForValue().setIfAbsent(TOTAL_PREFIX + id, total);
  }

  public List<String> atomicDecreaseStock(Map<Long, Integer> productQuantities) {
    if (productQuantities == null || productQuantities.isEmpty()) {
      return Collections.emptyList();
    }

    try {
      List<String> keys = new ArrayList<>();
      List<String> args = new ArrayList<>();

      args.add(TOTAL_PREFIX);
      args.add(USED_PREFIX);

      for (Map.Entry<Long, Integer> entry : productQuantities.entrySet()) {
        Long productId = entry.getKey();
        Integer quantity = entry.getValue();

        keys.add(productId.toString());

        args.add(String.valueOf(quantity));
      }

      List<String> result =
          stringRedisTemplate.execute(
              ProductLuaScriptProvider.getStockDecreaseScript(), keys, args.toArray());

      log.debug("재고 차감 스크립트 실행 결과: Id-{}, Count-{}", result.get(0), result.get(1));
      return result != null ? result : Collections.emptyList();
    } catch (Exception e) {
      log.error("재고 차감 중 오류 발생: {}", getRedisErrorMessage(e));
      throw new RedisSystemException("재고 차감 실패: " + getRedisErrorMessage(e), e);
    }
  }

  private String getRedisErrorMessage(Throwable e) {
    Throwable root = e;
    while (root.getCause() != null) {
      root = root.getCause();
    }

    return root.getMessage() != null ? root.getMessage() : e.getMessage();
  }
}
