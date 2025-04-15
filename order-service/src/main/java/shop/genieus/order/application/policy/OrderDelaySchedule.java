package shop.genieus.order.application.policy;

import java.time.LocalDateTime;

public record OrderDelaySchedule(Long orderId, LocalDateTime scheduledAt) {
  /**
   * 지정된 주문 ID와 기준 시간, 지연 분을 기반으로 예약 일정을 생성합니다.
   *
   * @param orderId 주문의 고유 식별자
   * @param targetTime 예약의 기준이 되는 시간
   * @param delayMinutes 기준 시간에 더할 지연 시간(분)
   * @return 지연된 예약 시간이 반영된 OrderDelaySchedule 인스턴스
   */
  public static OrderDelaySchedule of(Long orderId, LocalDateTime targetTime, long delayMinutes) {
    return new OrderDelaySchedule(orderId, targetTime.plusMinutes(delayMinutes));
  }
}
