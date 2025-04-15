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
import shop.genieus.order.application.out.util.OrderTimePort;
import shop.genieus.order.application.policy.OrderCancelPolicy;
import shop.genieus.order.application.policy.OrderDelaySchedule;
import shop.genieus.order.domain.event.OrderCreatedEvent;

@Slf4j
@Component
@RequiredArgsConstructor
public class OrderEventHandler {
  private final OrderTimePort orderTimePort;
  private final OrderCancelPolicy orderCancelPolicy;
  private final OrderEventSendPort orderEventSendPort;
  private final OrderDelayQueuePort orderDelayQueuePort;

  /**
   * 주문 생성 이벤트 발생 후 외부로 주문 생성 이벤트를 발행합니다.
   *
   * @param event 주문 생성 이벤트 객체
   */
  @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
  public void sendOrderCreated(OrderCreatedEvent event) {
    orderEventSendPort.sendOrderCreated(event);
    log.info("[sendOrderCreated] 주문생성 이벤트 발행: {}", event);
  }

  /**
   * 주문 생성 이벤트 발생 시 주문의 딜레이 큐 스케줄을 생성하여 저장합니다.
   *
   * 주문 ID, 주문 시간, 정책에 따른 대기 시간을 기반으로 딜레이 큐 스케줄을 생성하고 저장소에 등록합니다.
   *
   * @param event 주문 생성 도메인 이벤트
   */
  @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
  public void saveDelayQueue(OrderCreatedEvent event) {
    Long orderId = event.orderId();
    LocalDateTime orderedAt = event.orderedAt();
    long orderPendingMinutes = orderCancelPolicy.getOrderPendingMinutes();
    OrderDelaySchedule schedule = OrderDelaySchedule.of(orderId, orderedAt, orderPendingMinutes);
    orderDelayQueuePort.save(schedule);
    log.info("[saveDelayQueue] 주문생성 후 딜레이 큐 저장 : {}", event);
  }

  /**
   * 주문 취소 이벤트 발생 시 외부로 주문 취소 이벤트를 발행합니다.
   *
   * @param event 주문 취소 이벤트 정보
   */
  @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
  public void sendOrderCanceled(OrderCanceledEvent event) {
    orderEventSendPort.sendOrderCanceledEvent(event);
    log.info("[sendOrderCanceled] 주문취소 이벤트 발행 : {}", event);
  }

  /**
   * 주문 완료 시 해당 주문을 딜레이 큐에서 삭제합니다.
   *
   * @param event 주문 완료 이벤트
   */
  @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
  public void deleteDelayQueue(OrderCompletedEvent event) {
    Long orderId = event.orderId();
    orderDelayQueuePort.delete(orderId);
    log.info("[deleteDelayQueue] 딜레이 큐에서 주문 삭제: {}", event);
  }
}
