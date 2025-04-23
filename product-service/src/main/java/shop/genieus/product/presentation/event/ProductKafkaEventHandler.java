package shop.genieus.product.presentation.event;

import com.genieus.common.event.annotation.EventTypeMapping;
import com.genieus.common.event.order.OrderCanceledEvent;
import com.genieus.common.event.order.OrderCompletedEvent;
import com.genieus.common.event.order.OrderExpiredEvent;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import shop.genieus.product.application.in.command.ProductStockCommandService;
import shop.genieus.product.application.system.dto.OrderCompletedCommand;
import shop.genieus.product.presentation.event.mapper.ProductEventMapper;

@Slf4j
@Component
@RequiredArgsConstructor
public class ProductKafkaEventHandler {
  private final ProductStockCommandService commandService;
  private final ProductEventMapper mapper;

  @EventTypeMapping(topic = "order-events")
  public void handleOrderCanceled(OrderCanceledEvent event) {
    log.info("[handleOrderCanceled] 주문 취소 이벤트 수신 : {}", event);

    try {
      commandService.restockProducts(mapper.toRestoreStockCommand(event));
    } catch (Exception ex) {
      log.warn("주문 취소 이벤트 처리 실패: {}", ex.getMessage());
    }

    log.info("[handleOrderCanceled] 주문 취소 이벤트 컨슘 완료, 주문 아이디: {}", event.orderId());
  }

  @EventTypeMapping(topic = "order-events")
  public void handleOrderExpiredEvent(OrderExpiredEvent event) {
    log.info("[handleOrderExpired] 주문 만료 이벤트 수신 : {}", event);

    try {
      commandService.restockProducts(mapper.toRestoreStockCommand(event));
    } catch (Exception ex) {
      log.warn("주문 만료 이벤트 처리 실패: {}", ex.getMessage());
    }

    log.info("[handleOrderExpired] 주문 만료 이벤트 컨슘 완료, 주문 아이디: {}", event.orderId());
  }

  @EventTypeMapping(topic = "order-events")
  public void handleOrderCompleted(OrderCompletedEvent event) {
    log.info("[handleOrderCompleted] 주문 완료 이벤트 수신 : {}", event);

    try {
      OrderCompletedCommand command = mapper.toOrderCompletedCommand(event);
      List<String> results = commandService.totalDecreaseStock(command);
      log.info("[handleOrderCompletedEvent] 총재고 감소 상품 개수: {}", results.size() / 2);
    } catch (Exception ex) {
      log.warn("주문 완료 이벤트 처리 실패: {}", ex.getMessage());
    }

    log.info("[handleOrderCompletedEvent] 주문 완료 이벤트 컨슘 완료, 주문 아이디: {}", event.orderId());
  }
}
