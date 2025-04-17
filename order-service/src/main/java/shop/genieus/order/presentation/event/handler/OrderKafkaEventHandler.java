package shop.genieus.order.presentation.event.handler;

import com.genieus.common.event.annotation.EventTypeMapping;
import com.genieus.common.event.order.OrderCancelTriggerEvent;
import com.genieus.common.event.payment.PaymentCompletedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import shop.genieus.order.application.in.command.OrderCommandService;
import shop.genieus.order.application.in.command.dto.CancelOrderCommand;
import shop.genieus.order.application.in.command.dto.CompleteOrderCommand;
import shop.genieus.order.domain.event.OrderCreatedEvent;

@Slf4j
@Component
@RequiredArgsConstructor
public class OrderKafkaEventHandler {
  private final OrderCommandService orderCommandService;

  @EventTypeMapping(topic = "order-events")
  public void handleOrderCreated(OrderCreatedEvent event) {
    log.info("[handleOrderCreated] 주문 생성 이벤트 수신 : {}", event);
  }

  @EventTypeMapping(topic = "order-events")
  public void handleOrderCancelTrigger(OrderCancelTriggerEvent event) {
    log.info("[handleOrderCancelTrigger] 주문 취소 트리거 이벤트 수신 : {}", event);
    CancelOrderCommand command = new CancelOrderCommand(-1L, event.orderId());
    orderCommandService.cancelOrderBySystem(command);
  }

  @EventTypeMapping(topic = "payment-events")
  public void handlePaymentCompleted(PaymentCompletedEvent event) {
    log.info("[handlePayment] 결제 완료 이벤트 수신: {}", event);
    CompleteOrderCommand command = new CompleteOrderCommand(event.orderId());
    orderCommandService.completeOrder(command);
  }
}
