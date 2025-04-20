package shop.genieus.product.application.in.command;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import shop.genieus.product.application.in.command.dto.CreateProductCommand;
import shop.genieus.product.application.in.command.dto.ListProductCommand;
import shop.genieus.product.application.in.command.dto.RestoreStockCommand;
import shop.genieus.product.application.in.command.dto.RestoreStockCommand.RestoreStockItem;
import shop.genieus.product.application.in.command.dto.ValidateProductCommand;
import shop.genieus.product.application.in.command.dto.ValidateProductCommand.StockValidationItem;
import shop.genieus.product.application.out.cache.ProductCachePort;
import shop.genieus.product.application.out.persistence.ProductCommandPort;
import shop.genieus.product.application.out.support.time.ProductTimePort;
import shop.genieus.product.domain.model.ProductView;
import shop.genieus.product.domain.model.entity.Product;
import shop.genieus.product.domain.model.entity.StockEvent;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class ProductCommandService {

  private final ProductCommandPort productCommandPort;
  private final ProductCachePort productCachePort;
  private final ProductTimePort productTimePort;

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

  public List<ProductView> checkStockAvailability(ValidateProductCommand command) {
    Map<Long, Integer> requestedQuantities =
        aggregateQuantities(
            command.validateProductStocks(),
            StockValidationItem::id,
            StockValidationItem::quantity);

    try {
      List<ProductView> cachedViews =
          productCachePort.validateAndDecreaseStock(requestedQuantities);
      log.info("재고 예약 성공: {}", requestedQuantities);

      return cachedViews;
    } catch (Exception e) {
      log.error("재고 예약 실패: {}", e.getMessage());
      throw e;
    }
  }

  public void restockProducts(RestoreStockCommand command) {
    Map<Long, Integer> restoredQuantities =
        aggregateQuantities(
            command.items(), RestoreStockItem::productId, RestoreStockItem::quantity);

    List<StockEvent> events = createStockEvents(restoredQuantities, command.orderId());

    try {
      productCachePort.restoreStock(events);
      log.info("재고 복구 성공");
    } catch (Exception e) {
      log.error("재고 복구 실패: {}", e.getMessage());
      throw e;
    }
  }

  private <T> Map<Long, Integer> aggregateQuantities(
      List<T> items, Function<T, Long> idExtractor, Function<T, Integer> quantityExtractor) {
    return items.stream().collect(Collectors.toMap(idExtractor, quantityExtractor, Integer::sum));
  }

  private List<StockEvent> createStockEvents(Map<Long, Integer> aggregated, Long orderId) {
    long timestamp = productTimePort.currentTimeMillis();
    return aggregated.entrySet().stream()
        .map(
            entry ->
                StockEvent.createIncreaseEvent(
                    entry.getKey(), orderId, entry.getValue(), timestamp))
        .toList();
  }
}
