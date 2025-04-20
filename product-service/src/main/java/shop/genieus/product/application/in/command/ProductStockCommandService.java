package shop.genieus.product.application.in.command;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import shop.genieus.product.application.in.command.dto.RestoreStockCommand;
import shop.genieus.product.application.in.command.dto.ValidateProductCommand;
import shop.genieus.product.application.out.cache.ProductCachePort;
import shop.genieus.product.application.out.support.time.ProductTimePort;
import shop.genieus.product.application.system.dto.OrderCompletedCommand;
import shop.genieus.product.domain.model.ProductView;
import shop.genieus.product.domain.model.entity.StockEvent;
import shop.genieus.product.global.exception.ProductException;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

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

    public void restockProducts(RestoreStockCommand command) {
        Map<Long, Integer> restoredQuantities =
                aggregateQuantities(
                        command.items(), RestoreStockCommand.RestoreStockItem::productId, RestoreStockCommand.RestoreStockItem::quantity);

        List<StockEvent> events = createStockEvents(restoredQuantities, command.orderId());

        try {
            productCachePort.restoreStock(events);
            log.info("재고 복구 성공");
        } catch (Exception e) {
            log.error("재고 복구 실패: {}", e.getMessage());
            throw e;
        }
    }

    public List<String> totalDecreaseStock(OrderCompletedCommand command) {
        validateOrderCompletedCommand(command);

        Map<Long, Integer> decreaseQuantities =
                aggregateQuantities(
                        command.orderProductItems(),OrderCompletedCommand.OrderProductItem::productId,
                        OrderCompletedCommand.OrderProductItem::quantity);

        return productCachePort.totalDecreaseStock(decreaseQuantities, command.completedAt(),command.orderId());
    }

    private void validateOrderCompletedCommand(OrderCompletedCommand command) {
        if(command.completedAt() == null) {
            log.error("주문 날짜는 필수입니다.");
            throw new ProductException("주문 날짜는 필수입니다");
        }

        if(command.orderId() == null) {
            log.error("주문ID 는 필수입니다.");
            throw new ProductException("주문ID는 필수입니다.");
        }

        if(command.orderProductItems() == null || command.orderProductItems().isEmpty()) {
            log.error("주문된 상품 목록이 존재하지 않습니다.");
            throw new ProductException("주문된 상품 목록이 존재하지 않습니다.");
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
