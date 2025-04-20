package shop.genieus.order.infrastructure.event.external.queue;

import java.time.ZoneOffset;
import java.util.Collections;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.stereotype.Component;
import shop.genieus.order.application.out.persistence.OrderDelayQueuePort;
import shop.genieus.order.application.policy.OrderDelaySchedule;

@Slf4j
@Component
@RequiredArgsConstructor
public class OrderDelayQueueAdapter implements OrderDelayQueuePort {
  private static final String KEY = "order:delay-queue";
  private final RedisTemplate<String, Long> redisTemplate;
  private final RedisScript<List> popExpiredScript;

  @Override
  public void save(OrderDelaySchedule schedule) {
    try {
      Long orderId = schedule.orderId();
      double score = schedule.scheduledAt().atZone(ZoneOffset.UTC).toEpochSecond();
      redisTemplate.opsForZSet().add(KEY, orderId, score);
    } catch (Exception e) {
      log.error("[save] 저장 중 예외발생: {}, schedule: {}", e.getMessage(), schedule);
    }
  }

  @Override
  public List<Long> popExpiredOrders(long epochSecond) {
    try {
      List<Long> rawIds =
          redisTemplate.execute(
              popExpiredScript, Collections.singletonList(KEY), String.valueOf(epochSecond));
      if (rawIds.isEmpty()) {
        return List.of();
      }
      return rawIds;
    } catch (Exception e) {
      log.error("[popExpiredOrders] pop 중 예외발생: {}, epochSecond: {}", e.getMessage(), epochSecond);
    }
    return List.of();
  }

  @Override
  public void delete(Long orderId) {
    try {
      redisTemplate.opsForZSet().remove(KEY, orderId);
    } catch (Exception e) {
      log.error("[delete] 삭제 중 예외발생: {}, orderId: {}", e.getMessage(), orderId);
    }
  }
}
