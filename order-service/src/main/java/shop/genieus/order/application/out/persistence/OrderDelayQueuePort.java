package shop.genieus.order.application.out.persistence;

import java.util.Set;
import shop.genieus.order.application.policy.OrderDelaySchedule;

public interface OrderDelayQueuePort {
  void save(OrderDelaySchedule schedule);

  Set<Long> findExpiredEvents(long untilEpochSeconds);

  void delete(Long orderId);
}
