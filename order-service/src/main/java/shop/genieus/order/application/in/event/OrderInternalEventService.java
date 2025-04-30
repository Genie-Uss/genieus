package shop.genieus.order.application.in.event;

import com.genieus.common.event.order.CouponRestoredEvent;
import com.genieus.common.event.order.OrderCanceledEvent;
import com.genieus.common.event.order.OrderCompletedEvent;
import com.genieus.common.event.order.OrderCreationFailedEvent;
import com.genieus.common.event.order.OrderExpiredEvent;
import com.genieus.common.event.order.OrderPaymentRequestedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import shop.genieus.order.application.out.event.OrderExternalEventPort;
import shop.genieus.order.application.out.persistence.OrderDelayQueuePort;
import shop.genieus.order.application.out.util.OrderTimePort;
import shop.genieus.order.application.policy.OrderDelaySchedule;
import shop.genieus.order.domain.event.OrderCreatedEvent;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class OrderInternalEventService {
  private final OrderTimePort timePort;
  private final OrderDelayQueuePort delayQueuePort;
  private final OrderExternalEventPort externalEventPort;

  // ------------ BeforeCommit --------
  public void onOrderCreatedBeforeCommit(OrderCreatedEvent event) {
    long epochSecond = timePort.toEpochSecond(event.deadlineAt());
    OrderDelaySchedule schedule = new OrderDelaySchedule(event.orderId(), epochSecond);
    delayQueuePort.save(schedule);
  }

  public void onOrderCanceledBeforeCommit(OrderCanceledEvent event) {
    delayQueuePort.delete(event.orderId());
  }

  public void onPaymentRequestedBeforeCommit(OrderPaymentRequestedEvent event) {
    delayQueuePort.delete(event.orderId());
  }

  // ------------ AfterCommit --------
  public void onOrderCanceledAfterCommit(OrderCanceledEvent event) {
    externalEventPort.sendOrderCanceledEvent(event);
  }

  public void onOrderExpiredAfterCommit(OrderExpiredEvent event) {
    externalEventPort.sendOrderExpiredEvent(event);
  }

  public void onOrderCompletedAfterCommit(OrderCompletedEvent event) {
    externalEventPort.sendOrderCompletedEvent(event);
  }

  // ------------ AfterRollback --------
  public void onOrderCreationFailedAfterRollback(OrderCreationFailedEvent event) {
    externalEventPort.sendOrderCreationFailedEvent(event);
  }

  public void onCouponRestoredAfterRollback(CouponRestoredEvent event) {
    externalEventPort.sendCouponRestoredEvent(event);
  }
}
