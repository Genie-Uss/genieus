package shop.genieus.order.application.policy;

public record OrderDelaySchedule(Long orderId, long epochSecond) {}
