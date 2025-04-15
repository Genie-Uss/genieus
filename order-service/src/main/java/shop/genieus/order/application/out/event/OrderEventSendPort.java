package shop.genieus.order.application.out.event;

import com.genieus.common.event.order.OrderCanceledEvent;
import shop.genieus.order.domain.event.OrderCancelTriggerEvent;
import shop.genieus.order.domain.event.OrderCreatedEvent;

public interface OrderEventSendPort {
  void sendOrderCanceledEvent(OrderCanceledEvent event);

  void sendOrderCancelTriggerEvent(OrderCancelTriggerEvent event);

  void sendOrderCreatedEvent(OrderCreatedEvent event);
}
