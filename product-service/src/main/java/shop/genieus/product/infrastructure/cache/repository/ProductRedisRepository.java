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

  public Long getUsedStock(Long id) {
    return Optional.ofNullable(longRedisTemplate.opsForValue().get(USED_PREFIX + id)).orElse(0L);
  }

  public Long getTotalStock(Long id) {
    return Optional.ofNullable(longRedisTemplate.opsForValue().get(TOTAL_PREFIX + id)).orElse(0L);
  }

  public void setTotalStock(Long id, Long total) {
    longRedisTemplate.opsForValue().setIfAbsent(TOTAL_PREFIX + id, total);
  }

  public void setStatus(Long id, String status) {
    stringRedisTemplate.opsForValue().setIfAbsent(STATUS_PREFIX + id, status);
  }

  public List<String> atomicDecreaseStock(Map<Long, Integer> productQuantities) {
    if (productQuantities == null || productQuantities.isEmpty()) {
      return Collections.emptyList();
    }

    try {
      List<String> keys =
          productQuantities.keySet().stream().map(String::valueOf).collect(Collectors.toList());

      List<String> args = new ArrayList<>(2 + productQuantities.size());
      args.add(TOTAL_PREFIX);
      args.add(USED_PREFIX);
      productQuantities.values().forEach(qty -> args.add(String.valueOf(qty)));

      List<String> result =
          stringRedisTemplate.execute(
              ProductLuaScriptProvider.getStockDecreaseScript(), keys, args.toArray());

      if (result != null && !result.isEmpty()) {
        log.debug("재고 차감 스크립트 실행 결과: {}", result);
      }

      return result != null ? result : Collections.emptyList();

    } catch (Exception e) {
      String message = extractRedisErrorMessage(e);
      log.error("재고 차감 중 오류 발생: {}", message);
      throw new ProductException("재고 차감 실패: " + message, e);
    }
  }

  private String extractRedisErrorMessage(Throwable e) {
    Throwable root = e;
    while (root.getCause() != null) {
      root = root.getCause();
    }
    return Optional.ofNullable(root.getMessage()).orElse(e.getMessage());
  }
}
