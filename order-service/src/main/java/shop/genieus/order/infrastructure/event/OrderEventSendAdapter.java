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

  /**
   * 주문 취소 이벤트를 Kafka를 통해 발행합니다.
   *
   * @param event 발행할 주문 취소 이벤트
   */
  @Override
  public void sendOrderCanceledEvent(OrderCanceledEvent event) {
    EventEnvelope<OrderCanceledEvent> envelope = EventEnvelope.create(event);
    String key = String.valueOf(event.orderId());
    orderKafkaProducer.publish(key, envelope);
  }

  /**
   * 주문 취소 트리거 이벤트를 Kafka를 통해 발행합니다.
   *
   * @param event 발행할 주문 취소 트리거 이벤트
   */
  @Override
  public void sendOrderCancelTriggerEvent(OrderCancelTriggerEvent event) {
    EventEnvelope<OrderCancelTriggerEvent> envelope = EventEnvelope.create(event);
    String key = String.valueOf(event.orderId());
    orderKafkaProducer.publish(key, envelope);
  }

  /**
   * 주문 생성 이벤트를 Kafka를 통해 발행합니다.
   *
   * @param event 발행할 주문 생성 이벤트
   */
  @Override
  public void sendOrderCreated(OrderCreatedEvent event) {
    EventEnvelope<OrderCreatedEvent> envelope = EventEnvelope.create(event);
    String key = String.valueOf(event.orderId());
    orderKafkaProducer.publish(key, envelope);
  }
}
