package shop.genieus.product.presentation.event;

import com.genieus.common.event.annotation.EventTypeMapping;
import com.genieus.common.event.order.OrderCanceledEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import shop.genieus.product.application.in.command.ProductCommandService;
import shop.genieus.product.presentation.event.mapper.ProductEventMapper;

@Slf4j
@Component
@RequiredArgsConstructor
public class ProductKafkaEventHandler {
  private final ProductCommandService commandService;
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
}
