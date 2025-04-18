package shop.genieus.product.infrastructure.cache;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import shop.genieus.product.application.out.cache.ProductCachePort;
import shop.genieus.product.domain.model.ProductView;
import shop.genieus.product.domain.model.entity.Product;
import shop.genieus.product.infrastructure.cache.repository.ProductRedisRepository;

@Slf4j
@Component
@RequiredArgsConstructor
public class ProductCacheAdapter implements ProductCachePort {

  private final ProductRedisRepository productRedisRepository;
  private final ObjectMapper objectMapper;

  @Override
  public ProductView findProductViewById(Long productId) {
    return productRedisRepository
        .findProductViewById(productId)
        .orElseGet(
            () -> {
              log.info("캐시에서 상품 뷰 정보를 찾을 수 없습니다. productId: {}", productId);
              return null;
            });
  }

  @Override
  public Map<Long, ProductView> findProductViewListByIds(List<Long> productIds) {
    if (productIds == null || productIds.isEmpty()) {
      return Map.of();
    }

    List<ProductView> views = productRedisRepository.findProductViewListByIds(productIds);

    Map<Long, ProductView> result = new HashMap<>();
    for (int i = 0; i < productIds.size(); i++) {
      if (i < views.size() && views.get(i) != null) {
        result.put(productIds.get(i), views.get(i));
      }
    }

    return result;
  }

  @Override
  public void saveProduct(Long productId, Product product) {
    ProductView productView = ProductView.from(product);
    productRedisRepository.saveProductView(productId, productView);
    setTotalStock(productId, (long) product.getProductTotalStock());
    setStatus(productId, product.getProductStatus().name());
  }

  @Override
  public void saveProductBatch(List<Product> products) {
    if (products == null || products.isEmpty()) return;

    Map<Long, ProductView> productViewMap = new HashMap<>();
    Map<Long, Long> totalStockMap = new HashMap<>();

    for (Product product : products) {
      ProductView productView = ProductView.from(product);
      productViewMap.put(product.getProductId(), productView);
      totalStockMap.put(product.getProductId(), (long) product.getProductTotalStock());
    }

    productRedisRepository.saveProductViewBatch(productViewMap);

    for (Map.Entry<Long, Long> entry : totalStockMap.entrySet()) {
      setTotalStock(entry.getKey(), entry.getValue());
    }
  }

  @Override
  public void setTotalStock(Long productId, Long totalStock) {
    productRedisRepository.setTotalStock(productId, totalStock);
  }

  @Override
  public void setStatus(Long productId, String status) {
    productRedisRepository.setStatus(productId, status);
  }

  @Override
  public List<String> decreaseStock(Map<Long, Integer> productQuantities) {
    List<String> results = productRedisRepository.atomicDecreaseStock(productQuantities);

    for (int i = 0; i + 1 < results.size(); i += 2) {
      String productId = results.get(i);
      String usedStock = results.get(i + 1);
      log.info("상품 ID {} 재고 차감 완료. 총 사용량: {}", productId, usedStock);
    }

    return results;
  }
}
