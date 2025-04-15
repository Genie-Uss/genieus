package shop.genieus.product.application.in.command;

import java.util.*;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import shop.genieus.product.application.in.command.dto.CreateProductCommand;
import shop.genieus.product.application.in.command.dto.ListProductCommand;
import shop.genieus.product.application.in.command.dto.StockValidationResult;
import shop.genieus.product.application.in.command.dto.ValidateProductCommand;
import shop.genieus.product.application.out.cache.ProductCachePort;
import shop.genieus.product.application.out.persistence.ProductCommandPort;
import shop.genieus.product.domain.model.ProductView;
import shop.genieus.product.domain.model.entity.Product;
import shop.genieus.product.global.exception.InsufficientStockException;
import shop.genieus.product.global.exception.ProductUnavailableException;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class ProductCommandService {

  private final ProductCommandPort productCommandPort;
  private final ProductCachePort productCachePort;

  public Product createProduct(CreateProductCommand command) {
    Product product =
        Product.create(
            command.productName(),
            command.productText(),
            command.productPrice(),
            command.productTotalStock(),
            command.productStatus());

    Product saved = productCommandPort.save(product);
    productCachePort.saveProduct(saved.getProductId(), saved);
    return saved;
  }

  public List<Product> findProductListByIds(ListProductCommand command) {
    return productCommandPort.findProductsByIds(command.productIds());
  }

  public List<StockValidationResult> checkStockAvailability(ValidateProductCommand command) {
    List<ValidateProductCommand.StockValidationItem> items = command.validateProductStocks();
    Map<Long, Integer> requestedQuantities = aggregateStockRequests(items);
    List<Long> productIds = new ArrayList<>(requestedQuantities.keySet());

    // 1. 캐시 우선 조회
    Map<Long, ProductView> cachedViews = productCachePort.findProductViewListByIds(productIds);

    // 2. 캐시 누락된 ID 조회 및 캐싱
    List<Long> uncachedIds =
        productIds.stream().filter(id -> !cachedViews.containsKey(id)).toList();
    Map<Long, Product> dbProducts = fetchAndCacheProducts(uncachedIds, cachedViews);

    // 3. 전체 상품 판매 가능 여부 검증
    validateAllProductsAvailable(productIds, cachedViews);

    // 4. 총 재고 캐시 보완
    supplementTotalStock(cachedViews, dbProducts);

    try {
      // 5. 재고 차감
      productCachePort.decreaseStock(requestedQuantities);
      log.info("재고 차감 성공: {}", requestedQuantities);

      // 6. 결과 반환
      return productIds.stream()
          .map(cachedViews::get)
          .filter(Objects::nonNull)
          .map(StockValidationResult::from)
          .toList();

    } catch (Exception e) {
      log.error("재고 차감 실패: {}", e.getMessage());
      throw e;
    }
  }

  private Map<Long, Integer> aggregateStockRequests(
      List<ValidateProductCommand.StockValidationItem> items) {
    return items.stream()
        .collect(
            Collectors.toMap(
                ValidateProductCommand.StockValidationItem::id,
                ValidateProductCommand.StockValidationItem::quantity,
                Integer::sum));
  }

  private Map<Long, Product> fetchAndCacheProducts(List<Long> ids, Map<Long, ProductView> cache) {
    if (ids.isEmpty()) return Collections.emptyMap();

    List<Product> found = productCommandPort.findProductsByIds(ids);
    if (found.isEmpty()) return Collections.emptyMap();

    productCachePort.saveProductBatch(found);

    for (Product product : found) {
      cache.put(product.getProductId(), ProductView.from(product));
    }

    return found.stream().collect(Collectors.toMap(Product::getProductId, p -> p));
  }

  private void validateAllProductsAvailable(List<Long> ids, Map<Long, ProductView> views) {
    boolean hasUnavailable =
        ids.stream()
            .anyMatch(
                id -> {
                  ProductView view = views.get(id);
                  return view == null || !view.isOnSale();
                });

    if (hasUnavailable) {
      log.warn("판매 불가 상품 포함: {}", ids);
      throw new ProductUnavailableException();
    }
  }

  private void supplementTotalStock(Map<Long, ProductView> views, Map<Long, Product> dbMap) {
    for (Map.Entry<Long, ProductView> entry : views.entrySet()) {
      Long id = entry.getKey();
      ProductView view = entry.getValue();

      if (view.getProductTotalStock() == null) {
        Product dbProduct = dbMap.get(id);
        if (dbProduct == null) {
          log.warn("총 재고 정보 없음: {}", id);
          throw new InsufficientStockException();
        }

        productCachePort.setTotalStock(id, (long) dbProduct.getProductTotalStock());
      }
    }
  }
}
