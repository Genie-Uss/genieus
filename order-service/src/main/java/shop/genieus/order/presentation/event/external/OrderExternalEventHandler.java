package shop.genieus.order.presentation.event.external;

import com.genieus.common.event.DeadLetterEnvelope;
import com.genieus.common.event.EventEnvelope;
import com.genieus.common.event.annotation.EventTypeMapping;
import com.genieus.common.event.annotation.FallbackMapping;
import com.genieus.common.event.payment.PaymentCompletedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import shop.genieus.order.application.in.command.OrderCommandService;
import shop.genieus.order.application.in.command.dto.CompleteOrderCommand;

@Slf4j
@Component
@RequiredArgsConstructor
public class OrderExternalEventHandler {
  private final OrderCommandService commandService;

  @EventTypeMapping(topic = "payment-events")
  public void handlePaymentCompleted(PaymentCompletedEvent event) {
    CompleteOrderCommand command = new CompleteOrderCommand(event.orderId());
    commandService.completeOrder(command);
  }

  @FallbackMapping(topic = "payment-events", eventType = "PaymentCompletedEvent")
  public void paymentCompletedFallback(
      EventEnvelope<PaymentCompletedEvent> envelope, Throwable ex) {
    log.warn("[paymentCompletedFallback] 결제 완료 실패 : {}, {}", envelope, ex.getMessage());
    DeadLetterEnvelope<PaymentCompletedEvent> deadLetterEnvelope =
        DeadLetterEnvelope.from(envelope, ex.getMessage());
  }
}
