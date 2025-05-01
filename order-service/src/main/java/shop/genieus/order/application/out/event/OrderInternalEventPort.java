package shop.genieus.order.application.out.event;

import shop.genieus.order.application.in.command.dto.CreateOrderCommand;
import shop.genieus.order.domain.model.entity.Order;

public interface OrderInternalEventPort {
  void publishOrderCreated(Order order);

  void publishOrderCanceled(Order order);

  void publishOrderExpired(Order order);

  void publishOrderCompleted(Order order);

  void publishPaymentRequested(Order order);

  void publishCouponUsed(Order order);

  void publishOrderCreationFailed(CreateOrderCommand command);
}
