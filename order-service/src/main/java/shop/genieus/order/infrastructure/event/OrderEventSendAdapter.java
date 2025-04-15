package shop.genieus.order.infrastructure.event;

import com.genieus.common.event.EventEnvelope;
import com.genieus.common.event.order.OrderCanceledEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import shop.genieus.order.application.out.event.OrderEventSendPort;
import shop.genieus.order.domain.event.OrderCancelTriggerEvent;
import shop.genieus.order.domain.event.OrderCreatedEvent;
import shop.genieus.order.infrastructure.event.producer.OrderKafkaProducer;

@Component
@RequiredArgsConstructor
public class OrderEventSendAdapter implements OrderEventSendPort {
  private final OrderKafkaProducer orderKafkaProducer;

  @Override
  public void sendOrderCanceledEvent(OrderCanceledEvent event) {
    EventEnvelope<OrderCanceledEvent> envelope = EventEnvelope.create(event);
    String key = String.valueOf(event.orderId());
    orderKafkaProducer.publish(key, envelope);
  }

  @Override
  public void sendOrderCancelTriggerEvent(OrderCancelTriggerEvent event) {
    EventEnvelope<OrderCancelTriggerEvent> envelope = EventEnvelope.create(event);
    String key = String.valueOf(event.orderId());
    orderKafkaProducer.publish(key, envelope);
  }

  @Override
  public void sendOrderCreated(OrderCreatedEvent event) {
    EventEnvelope<OrderCreatedEvent> envelope = EventEnvelope.create(event);
    String key = String.valueOf(event.orderId());
    orderKafkaProducer.publish(key, envelope);
  }
}
