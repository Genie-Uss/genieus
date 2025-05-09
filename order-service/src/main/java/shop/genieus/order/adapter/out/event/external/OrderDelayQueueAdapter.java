package shop.genieus.order.adapter.out.event.external;

import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import shop.genieus.order.adapter.out.event.external.redis.OrderRedisDelayQueue;
import shop.genieus.order.application.out.persistence.OrderDelayQueuePort;
import shop.genieus.order.application.policy.OrderDelaySchedule;

@Slf4j
@Component
@RequiredArgsConstructor
public class OrderDelayQueueAdapter implements OrderDelayQueuePort {

  private final OrderRedisDelayQueue redisDelayQueue;

  @Override
  public void save(OrderDelaySchedule schedule) {
    redisDelayQueue.save(schedule);
  }

  @Override
  public List<Long> popExpiredOrders(long epochSecond) {
    return redisDelayQueue.popExpiredOrders(epochSecond);
  }

  @Override
  public void delete(Long orderId) {
    redisDelayQueue.delete(orderId);
  }
}
