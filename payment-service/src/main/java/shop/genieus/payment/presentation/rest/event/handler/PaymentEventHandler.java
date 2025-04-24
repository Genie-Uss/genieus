package shop.genieus.payment.presentation.rest.event.handler;

import com.genieus.common.event.annotation.EventTypeMapping;
import com.genieus.common.event.order.OrderCanceledEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import shop.genieus.payment.application.in.command.PaymentCommandService;
import shop.genieus.payment.domain.model.entity.Payment;

@Slf4j
@Component
@RequiredArgsConstructor
public class PaymentEventHandler {

  private final PaymentCommandService paymentCommandService;

  @EventTypeMapping(topic = "order-events", eventType = "OrderCanceledEvent")
  public void handleOrderCanceledEvent(OrderCanceledEvent orderCanceledEvent) {
    log.warn("[이벤트 수신 - 주문 번호]: {}", orderCanceledEvent.orderId());
    Payment payment = paymentCommandService.cancel(orderCanceledEvent.orderId());
    log.info(
        "[결제 취소 완료] paymentId={}, status={}", payment.getPaymentId(), payment.getPaymentStatus());
  }
}
