package shop.genieus.order.application.out.persistence;

import java.time.LocalDateTime;
import java.util.List;
import shop.genieus.order.domain.model.entity.Order;

public interface OrderCommandPort {
  Order save(Order order);

  Order findById(Long orderId);

  List<Order> findAll(List<Long> orderIds);

  void expireAll(List<Long> orderIds, LocalDateTime expiredAt);
}
