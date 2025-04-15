package shop.genieus.order.domain.event;

import com.genieus.common.event.DomainEvent;

public record OrderCancelTriggerEvent(Long orderId) implements DomainEvent {
  public static OrderCancelTriggerEvent of(Long orderId) {
    return new OrderCancelTriggerEvent(orderId);
  }
}
