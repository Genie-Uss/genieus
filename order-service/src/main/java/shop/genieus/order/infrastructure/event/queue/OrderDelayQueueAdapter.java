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

  /**
   * 주문 지연 스케줄을 Redis 정렬 집합에 저장합니다.
   *
   * 주문 ID를 예약 실행 시간(UTC 기준 epoch 초)과 함께 Redis ZSET에 추가하여 지연 주문 이벤트를 관리합니다.
   *
   * @param schedule 저장할 주문 지연 스케줄 정보
   */
  @Override
  public void save(OrderDelaySchedule schedule) {
    try {
      Long orderId = schedule.orderId();
      double score = schedule.scheduledAt().atZone(ZoneOffset.UTC).toEpochSecond();
      redisTemplate.opsForZSet().add(ZSET_KEY, orderId, score);
    } catch (Exception e) {
      log.error("[OrderDelayQueueAdapter] 저장 중 예외발생: {}, schedule: {}", e.getMessage(), schedule);
    }
  }

  /**
   * 지정된 시간(에포크 초)까지 만료된 주문 이벤트의 ID 집합을 조회합니다.
   *
   * @param untilEpochSeconds 만료 기준이 되는 에포크 초(UTC)
   * @return 만료된 주문 이벤트의 ID 집합
   */
  @Override
  public Set<Long> findExpiredEvents(long untilEpochSeconds) {
    return redisTemplate.opsForZSet().rangeByScore(ZSET_KEY, 0, untilEpochSeconds);
  }

  /**
   * 지정한 주문 ID를 Redis 지연 큐에서 제거합니다.
   *
   * @param orderId 제거할 주문 ID
   */
  @Override
  public void delete(Long orderId) {
    try {
      redisTemplate.opsForZSet().remove(ZSET_KEY, orderId);
    } catch (Exception e) {
      log.error("[OrderDelayQueueAdapter] 삭제 중 예외발생: {}, orderId: {}", e.getMessage(), orderId);
    }
  }
}
