package shop.genieus.order.application.in.event;

import com.genieus.common.event.order.OrderCanceledEvent;
import com.genieus.common.event.order.OrderCompletedEvent;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;
import shop.genieus.order.application.out.event.OrderEventSendPort;
import shop.genieus.order.application.out.persistence.OrderDelayQueuePort;
import shop.genieus.order.application.policy.OrderCancelPolicy;
import shop.genieus.order.application.policy.OrderDelaySchedule;
import shop.genieus.order.domain.event.OrderCreatedEvent;

@Slf4j
@Component
@RequiredArgsConstructor
public class OrderEventHandler {
  private final OrderCancelPolicy orderCancelPolicy;
  private final OrderEventSendPort orderEventSendPort;
  private final OrderDelayQueuePort orderDelayQueuePort;

  @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
  public void sendOrderCreated(OrderCreatedEvent event) {
    try {
      orderEventSendPort.sendOrderCreatedEvent(event);
      log.info("[sendOrderCreatedEvent] 주문생성 이벤트 발행: {}", event);
    } catch (Exception e) {
      log.error("[sendOrderCreatedEvent] 주문생성 이벤트 발행 실패: {}, 오류: {}", event, e.getMessage());
      // todo 재시도 로직 추가
    }
  }

  @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
  public void saveDelayQueue(OrderCreatedEvent event) {
    try {
      Long orderId = event.orderId();
      LocalDateTime orderedAt = event.orderedAt();
      long orderPendingMinutes = orderCancelPolicy.getOrderPendingMinutes();
      OrderDelaySchedule schedule = OrderDelaySchedule.of(orderId, orderedAt, orderPendingMinutes);
      orderDelayQueuePort.save(schedule);
      log.info("[saveDelayQueue] 주문생성 후 딜레이 큐 저장 : {}", event);
    } catch (Exception e) {
      log.error("[saveDelayQueue] 딜레이 큐 저장 실패: {}, 오류: {}", event, e.getMessage());
      // todo 재시도 로직 추가
    }
  }

  @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
  public void sendOrderCanceled(OrderCanceledEvent event) {
    try {
      orderEventSendPort.sendOrderCanceledEvent(event);
      log.info("[sendOrderCanceled] 주문취소 이벤트 발행 : {}", event);
    } catch (Exception e) {
      log.error("[sendOrderCanceled] 주문생성 이벤트 발행 실패: {}, 오류: {}", event, e.getMessage());
      // todo 재시도 로직 추가
    }
  }

  @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
  public void deleteDelayQueue(OrderCompletedEvent event) {
    try {
      Long orderId = event.orderId();
      orderDelayQueuePort.delete(orderId);
      log.info("[deleteDelayQueue] 딜레이 큐 삭제: {}", event);
    } catch (Exception e) {
      log.error("[deleteDelayQueue] 딜레이 큐 삭제 실패: {}, 오류: {}", event, e.getMessage());
      // todo 재시도 로직 추가
    }
  }
}
