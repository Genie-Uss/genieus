package shop.genieus.order.application.out.event;

import com.genieus.common.event.order.OrderCanceledEvent;
import shop.genieus.order.domain.event.OrderCancelTriggerEvent;
import shop.genieus.order.domain.event.OrderCreatedEvent;

public interface OrderEventSendPort {
  /**
 * 주문이 취소되었음을 알리는 이벤트를 전송합니다.
 *
 * @param event 주문 취소 이벤트 정보
 */
void sendOrderCanceledEvent(OrderCanceledEvent event);

  /**
 * 주문 취소 트리거 이벤트를 전송합니다.
 *
 * @param event 주문 취소 트리거 이벤트 객체
 */
void sendOrderCancelTriggerEvent(OrderCancelTriggerEvent event);

  /**
 * 주문 생성 이벤트를 전송합니다.
 *
 * @param event 생성된 주문에 대한 이벤트 객체
 */
void sendOrderCreated(OrderCreatedEvent event);
}
