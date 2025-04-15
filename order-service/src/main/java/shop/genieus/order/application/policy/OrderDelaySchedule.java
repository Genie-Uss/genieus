package shop.genieus.order.application.policy;

import java.time.LocalDateTime;

public record OrderDelaySchedule(Long orderId, LocalDateTime scheduledAt) {
  public static OrderDelaySchedule of(Long orderId, LocalDateTime targetTime, long delayMinutes) {
    return new OrderDelaySchedule(orderId, targetTime.plusMinutes(delayMinutes));
  }
}
