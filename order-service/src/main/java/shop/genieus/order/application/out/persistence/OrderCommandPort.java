package shop.genieus.order.application.out.persistence;

import shop.genieus.order.domain.model.entity.Order;

public interface OrderCommandPort {
  Order save(Order order);

  Order findById(Long orderId);
}
