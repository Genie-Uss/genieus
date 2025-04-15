package shop.genieus.order.domain.event;

import com.genieus.common.event.DomainEvent;

public record OrderCancelTriggerEvent(Long orderId) implements DomainEvent {
  /**
   * 주어진 주문 ID로 OrderCancelTriggerEvent 인스턴스를 생성합니다.
   *
   * @param orderId 취소할 주문의 식별자
   * @return 생성된 OrderCancelTriggerEvent 객체
   */
  public static OrderCancelTriggerEvent of(Long orderId) {
    return new OrderCancelTriggerEvent(orderId);
  }
}
