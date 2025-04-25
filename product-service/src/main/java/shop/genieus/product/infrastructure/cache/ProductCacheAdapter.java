package shop.genieus.product.infrastructure.cache;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import shop.genieus.product.application.out.cache.ProductCachePort;
import shop.genieus.product.domain.model.ProductView;
import shop.genieus.product.domain.model.entity.Product;
import shop.genieus.product.domain.model.entity.StockEvent;
import shop.genieus.product.domain.model.vo.ProductStatus;
import shop.genieus.product.global.exception.ProductException;
import shop.genieus.product.global.exception.ProductNotFoundException;
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
    setInitialTotalStock(productId, (long) product.getProductTotalStock());
    setInitialStatus(productId, product.getProductStatus().name());
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
      setInitialTotalStock(entry.getKey(), entry.getValue());
    }
  }

  @Override
  public List<ProductView> validateAndDecreaseStock(Map<Long, Integer> productQuantities) {
    Map<Long, Integer> orderedQuantities = new LinkedHashMap<>(productQuantities);

    List<String> results = productRedisRepository.atomicValidateAndDecreaseStock(orderedQuantities);
    List<ProductView> productViews = new ArrayList<>(orderedQuantities.size());

    for (int i = 0; i + 2 < results.size(); i += 3) {
      String productId = results.get(i);
      String usedStock = results.get(i + 1);
      String metaInfo = results.get(i + 2);

      log.info("상품 ID {} 재고 차감 완료. 총 사용량: {}", productId, usedStock);

      if (metaInfo.isEmpty()) {
        log.warn("상품 ID {}의 메타 정보가 없습니다. 이는 정상적이지 않은 상태입니다.", productId);
        throw new ProductNotFoundException();
      }

      try {
        ProductView view = objectMapper.readValue(metaInfo, ProductView.class);
        productViews.add(view);
      } catch (Exception e) {
        log.error("메타 정보 변환 실패: {}", e.getMessage());
        throw new ProductException("메타 정보 변환 실패: " + productId, e);
      }
    }

    return productViews;
  }

  @Override
  public void restoreUsedStock(Map<Long, Integer> releasedQuantities) {
    if (releasedQuantities == null || releasedQuantities.isEmpty()) {
      return;
    }

    try {
      String resultLines =
          productRedisRepository.atomicDecreaseUsedProductStock(releasedQuantities);
      log.info(resultLines);
    } catch (ProductException e) {
      throw e;
    }
  }

  @Override
  public void restoreTotalStock(List<StockEvent> stockEvents) {
    if (stockEvents == null || stockEvents.isEmpty()) {
      return;
    }

    Map<Long, Integer> productQuantities =
        stockEvents.stream()
            .collect(Collectors.toMap(StockEvent::productId, StockEvent::quantity, Integer::sum));

    Long orderId = stockEvents.get(0).orderId();
    Long timestamp = stockEvents.get(0).timestamp();
    try {
      String results =
          productRedisRepository.atomicRestoreStockWithEvents(
              productQuantities, orderId, timestamp);

      log.info(results);
    } catch (ProductException e) {
      throw e;
    }
  }

  @Override
  public List<String> totalDecreaseStock(
      Map<Long, Integer> productQuantities, LocalDateTime completedAt, Long orderId) {
    try {
      return productRedisRepository.atomicTotalDecreaseStock(
          productQuantities, completedAt, orderId);
    } catch (ProductException e) {
      log.error("총재고 감소 중 오류 발생: orderId={}, 상세={}", orderId, e.getMessage());
      throw e;
    }
  }

  private void setInitialTotalStock(Long productId, Long totalStock) {
    productRedisRepository.setInitialTotalStock(productId, totalStock);
  }

  private void setInitialStatus(Long productId, String status) {
    if (ProductStatus.isValid(status)) {
      productRedisRepository.setInitialStatus(productId, status);
    } else {
      log.info("유효하지 않은 상품 상태입니다: {}", status);
    }
  }
}
