package shop.genieus.order.infrastructure.event.queue;

import java.time.ZoneOffset;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import shop.genieus.order.application.out.persistence.OrderDelayQueuePort;
import shop.genieus.order.application.policy.OrderDelaySchedule;

@Slf4j
@Component
@RequiredArgsConstructor
public class OrderDelayQueueAdapter implements OrderDelayQueuePort {
  private final RedisTemplate<String, Long> redisTemplate;
  private static final String ZSET_KEY = "order:delay:queue";

  @Override
  public void save(OrderDelaySchedule schedule) {
    try {
      Long orderId = schedule.orderId();
      double score = schedule.scheduledAt().atZone(ZoneOffset.UTC).toEpochSecond();
      redisTemplate.opsForZSet().add(ZSET_KEY, orderId, score);
    } catch (Exception e) {
      log.error("[save] 저장 중 예외발생: {}, schedule: {}", e.getMessage(), schedule);
    }
  }

  @Override
  public Set<Long> findExpiredEvents(long untilEpochSeconds) {
    try {
      return redisTemplate.opsForZSet().rangeByScore(ZSET_KEY, 0, untilEpochSeconds);
    } catch (Exception e) {
      log.error(
          "[findExpiredEvents] 만료된 이벤트 조회 중 예외발생: {}, untilEpochSeconds: {}",
          e.getMessage(),
          untilEpochSeconds);
      return Set.of();
    }
  }

  @Override
  public void delete(Long orderId) {
    try {
      redisTemplate.opsForZSet().remove(ZSET_KEY, orderId);
    } catch (Exception e) {
      log.error("[delete] 삭제 중 예외발생: {}, orderId: {}", e.getMessage(), orderId);
    }
  }
}
