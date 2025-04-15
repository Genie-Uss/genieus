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

    Product savedProduct = productCommandPort.save(product);
    productCachePort.saveProduct(savedProduct.getProductId(), savedProduct);

    return savedProduct;
  }

  public List<Product> findProductListByIds(ListProductCommand command) {
    return productCommandPort.findProductsByIds(command.productIds());
  }

  public List<StockValidationResult> checkStockAvailability(ValidateProductCommand command) {
    List<ValidateProductCommand.StockValidationItem> items = command.validateProductStocks();
    List<Long> productIds = extractProductIds(items);

    // 1. 캐시에서 상품 뷰 조회
    Map<Long, ProductView> cachedViews = productCachePort.findProductViewListByIds(productIds);

    // 2. 판매 불가능한 상품 체크
    validateAllProductsAvailable(productIds, cachedViews);

    // 3. 캐시에 없는 상품 DB 조회 및 캐싱
    List<Long> uncachedIds = findUncachedIds(productIds, cachedViews);
    Map<Long, Product> productMap = fetchAndCacheProducts(uncachedIds, cachedViews);

    // 4. 재고 차감 요청 집계
    Map<Long, Integer> aggregatedRequests = aggregateStockRequests(items);

    try {
      // 5. 재고 검증
      validateSufficientStock(aggregatedRequests, cachedViews, productMap);

      // 6. 재고 차감
      productCachePort.decreaseStock(aggregatedRequests);
      log.info("상품 재고 차감 성공: {}", aggregatedRequests);

      // 7. 결과 반환
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

  private List<Long> extractProductIds(List<ValidateProductCommand.StockValidationItem> items) {
    return items.stream().map(ValidateProductCommand.StockValidationItem::id).toList();
  }

  private List<Long> findUncachedIds(List<Long> productIds, Map<Long, ProductView> cachedViews) {
    return productIds.stream().filter(id -> !cachedViews.containsKey(id)).toList();
  }

  private void validateAllProductsAvailable(
      List<Long> productIds, Map<Long, ProductView> cachedViews) {
    List<Long> unavailableIds =
        productIds.stream()
            .filter(
                id -> {
                  ProductView view = cachedViews.get(id);
                  return view != null && !view.isOnSale();
                })
            .toList();

    if (!unavailableIds.isEmpty()) {
      log.warn("판매 중이 아닌 상품 ID: {}", unavailableIds);
      throw new ProductUnavailableException();
    }
  }

  private Map<Long, Product> fetchAndCacheProducts(
      List<Long> uncachedIds, Map<Long, ProductView> cachedViews) {

    if (uncachedIds.isEmpty()) {
      return Collections.emptyMap();
    }

    List<Product> dbProducts = productCommandPort.findProductsByIds(uncachedIds);

    if (!dbProducts.isEmpty()) {
      productCachePort.saveProductBatch(dbProducts);

      for (Product product : dbProducts) {
        cachedViews.put(product.getProductId(), ProductView.from(product));
      }
    }

    return dbProducts.stream().collect(Collectors.toMap(Product::getProductId, p -> p));
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

  private void validateSufficientStock(
      Map<Long, Integer> aggregatedRequests,
      Map<Long, ProductView> cachedViews,
      Map<Long, Product> productMap) {

    for (Long productId : aggregatedRequests.keySet()) {
      ProductView view = cachedViews.get(productId);

      if (view == null) {
        log.warn("상품 ID {}의 뷰 정보가 없습니다", productId);
        throw new ProductUnavailableException();
      }

      ensureTotalStockCached(productId, view, productMap);
    }
  }

  private void ensureTotalStockCached(
      Long productId, ProductView view, Map<Long, Product> productMap) {
    if (view.getProductTotalStock() == null) {
      if (productMap.containsKey(productId)) {
        productCachePort.setTotalStock(
            productId, (long) productMap.get(productId).getProductTotalStock());
      } else {
        log.warn("상품 ID {}의 총 재고량 정보가 없습니다", productId);
        throw new InsufficientStockException();
      }
    }
  }
}
