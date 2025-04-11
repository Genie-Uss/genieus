package shop.genieus.order.application.out.event;

import shop.genieus.order.domain.model.entity.Order;

public interface OrderEventSendPort {
  void sendOrderCanceledEvent(Order order);
}
