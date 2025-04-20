package shop.genieus.order.application.out.event;

import shop.genieus.order.domain.model.entity.Order;

public interface OrderInternalEventPort {
  void publishOrderCreated(Order order);

  void publishOrderCanceled(Order order);

  void publishOrderExpired(Order order);

  void publishPaymentCompleted(Order order);

  void publishPaymentRequested(Order order);
}
