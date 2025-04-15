package shop.genieus.order.application.out.persistence;

import java.util.Set;
import shop.genieus.order.application.policy.OrderDelaySchedule;

public interface OrderDelayQueuePort {
  /**
 * 주문 지연 스케줄 정보를 저장합니다.
 *
 * @param schedule 저장할 주문 지연 스케줄 정보
 */
void save(OrderDelaySchedule schedule);

  /**
 * 지정된 에포크 초(untilEpochSeconds)까지 만료된 주문 지연 이벤트의 주문 ID 집합을 반환합니다.
 *
 * @param untilEpochSeconds 만료 기준이 되는 에포크 초(UTC)
 * @return 만료된 주문 지연 이벤트의 주문 ID 집합
 */
Set<Long> findExpiredEvents(long untilEpochSeconds);

  /**
 * 지정한 주문 ID에 해당하는 지연 주문 이벤트를 삭제합니다.
 *
 * @param orderId 삭제할 주문의 ID
 */
void delete(Long orderId);
}
