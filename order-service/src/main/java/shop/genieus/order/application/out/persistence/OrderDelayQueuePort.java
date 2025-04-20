package shop.genieus.order.application.out.persistence;

import java.util.List;
import shop.genieus.order.application.policy.OrderDelaySchedule;

public interface OrderDelayQueuePort {
  void save(OrderDelaySchedule schedule);

  List<Long> popExpiredOrders(long epochSecond);

  void delete(Long orderId);
}
