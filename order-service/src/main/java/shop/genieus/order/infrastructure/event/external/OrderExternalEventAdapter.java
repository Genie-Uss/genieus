package shop.genieus.order.infrastructure.event.external;

import com.genieus.common.event.DomainEvent;
import com.genieus.common.event.EventEnvelope;
import com.genieus.common.event.order.CouponRestoredEvent;
import com.genieus.common.event.order.OrderCanceledEvent;
import com.genieus.common.event.order.OrderCompletedEvent;
import com.genieus.common.event.order.OrderCreationFailedEvent;
import com.genieus.common.event.order.OrderExpiredEvent;
import com.genieus.common.event.payment.PaymentCompletedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import shop.genieus.order.application.out.event.OrderExternalEventPort;
import shop.genieus.order.domain.event.OrderCreatedEvent;
import shop.genieus.order.infrastructure.event.external.producer.OrderKafkaProducer;

@Component
@RequiredArgsConstructor
public class OrderExternalEventAdapter implements OrderExternalEventPort {
  private final OrderKafkaProducer orderKafkaProducer;

  @Override
  public void sendOrderCanceledEvent(OrderCanceledEvent event) {
    sendOrderEventWithKey(event, event.orderId());
  }

  @Override
  public void sendOrderCreatedEvent(OrderCreatedEvent event) {
    sendOrderEventWithKey(event, event.orderId());
  }

  @Override
  public void sendOrderExpiredEvent(OrderExpiredEvent event) {
    sendOrderEventWithKey(event, event.orderId());
  }

  @Override
  public void sendOrderCompletedEvent(OrderCompletedEvent event) {
    sendOrderEventWithKey(event, event.orderId());
  }

  @Override
  public void sendPaymentCompletedEvent(PaymentCompletedEvent event) {
    sendOrderEventWithKey(event, event.orderId());
  }

  @Override
  public void sendCouponRestoredEvent(CouponRestoredEvent event) {
    sendOrderEventWithKey(event, event.orderId());
  }

  @Override
  public void sendOrderCreationFailedEvent(OrderCreationFailedEvent event) {
    sendOrderEvent(event);
  }

  private void sendOrderEventWithKey(DomainEvent event, Long orderId) {
    EventEnvelope<DomainEvent> envelope = EventEnvelope.create(event);
    String key = String.valueOf(orderId);
    orderKafkaProducer.publish(key, envelope);
  }

  private void sendOrderEvent(DomainEvent event) {
    EventEnvelope<DomainEvent> envelope = EventEnvelope.create(event);
    orderKafkaProducer.publish(envelope);
  }
}
