package shop.genieus.product.application.in.command;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import shop.genieus.product.application.in.command.dto.RestoreStockItem;
import shop.genieus.product.application.in.command.dto.RestoreTotalStockCommand;
import shop.genieus.product.application.in.command.dto.RestoreUsedStockCommand;
import shop.genieus.product.application.in.command.dto.ValidateProductCommand;
import shop.genieus.product.application.out.cache.ProductCachePort;
import shop.genieus.product.application.out.support.time.ProductTimePort;
import shop.genieus.product.application.system.dto.OrderCompletedCommand;
import shop.genieus.product.domain.model.ProductView;
import shop.genieus.product.domain.model.entity.StockEvent;
import shop.genieus.product.global.exception.ProductException;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductStockCommandService {
  private final ProductCachePort productCachePort;
  private final ProductTimePort productTimePort;

  public List<ProductView> checkStockAvailability(ValidateProductCommand command) {
    Map<Long, Integer> requestedQuantities =
        aggregateQuantities(
            command.validateProductStocks(),
            ValidateProductCommand.StockValidationItem::id,
            ValidateProductCommand.StockValidationItem::quantity);

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

  public void restoreUsedProductStock(RestoreUsedStockCommand command) {
    Map<Long, Integer> restoredQuantities =
        aggregateQuantities(
            command.items(), RestoreStockItem::productId, RestoreStockItem::quantity);

    try {
      productCachePort.restoreUsedStock(restoredQuantities);
      log.info("예약 재고 취소 성공");
    } catch (Exception e) {
      log.error("예약된 재고 취소 중 오류 발생: orderId={}, 상세={}", command.orderId(), e.getMessage());
      throw e;
    }
  }

  public void restoreTotalProductStock(RestoreTotalStockCommand command) {
    List<StockEvent> events = createStockEvents(command);

    try {
      productCachePort.restoreTotalStock(events);
      log.info("총 재고 복구 성공");
    } catch (Exception e) {
      log.error("총 재고 복구 중 오류 발생: orderId={}, 상세={}", command.orderId(), e.getMessage());
      throw e;
    }
  }

  public List<String> totalDecreaseStock(OrderCompletedCommand command) {
    validateOrderCompletedCommand(command);

    Map<Long, Integer> decreaseQuantities =
        aggregateQuantities(
            command.orderProductItems(),
            OrderCompletedCommand.OrderProductItem::productId,
            OrderCompletedCommand.OrderProductItem::quantity);

    return productCachePort.totalDecreaseStock(
        decreaseQuantities, command.completedAt(), command.orderId());
  }

  private void validateOrderCompletedCommand(OrderCompletedCommand command) {
    if (command.completedAt() == null) {
      log.error("주문 날짜는 필수입니다.");
      throw new ProductException("주문 날짜는 필수입니다");
    }

    if (command.orderId() == null) {
      log.error("주문ID 는 필수입니다.");
      throw new ProductException("주문ID는 필수입니다.");
    }

    if (command.orderProductItems() == null || command.orderProductItems().isEmpty()) {
      log.error("주문된 상품 목록이 존재하지 않습니다.");
      throw new ProductException("주문된 상품 목록이 존재하지 않습니다.");
    }
  }

  private <T> Map<Long, Integer> aggregateQuantities(
      List<T> items, Function<T, Long> idExtractor, Function<T, Integer> quantityExtractor) {
    return items.stream().collect(Collectors.toMap(idExtractor, quantityExtractor, Integer::sum));
  }

  private List<StockEvent> createStockEvents(RestoreTotalStockCommand command) {
    Map<Long, Integer> aggregateQuantities =
        aggregateQuantities(
            command.items(), RestoreStockItem::productId, RestoreStockItem::quantity);
    Long orderId = command.orderId();
    long timestamp = productTimePort.convertToMillis(command.canceledAt());

    return aggregateQuantities.entrySet().stream()
        .map(
            entry ->
                StockEvent.createIncreaseEvent(
                    entry.getKey(), orderId, entry.getValue(), timestamp))
        .toList();
  }
}
