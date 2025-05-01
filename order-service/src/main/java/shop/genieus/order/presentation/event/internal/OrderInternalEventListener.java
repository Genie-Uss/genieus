package shop.genieus.order.presentation.event.internal;

import com.genieus.common.event.order.CouponRestoredEvent;
import com.genieus.common.event.order.OrderCanceledEvent;
import com.genieus.common.event.order.OrderCompletedEvent;
import com.genieus.common.event.order.OrderCreationFailedEvent;
import com.genieus.common.event.order.OrderExpiredEvent;
import com.genieus.common.event.order.OrderPaymentRequestedEvent;
import io.micrometer.observation.annotation.Observed;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;
import shop.genieus.order.application.in.event.OrderInternalEventService;
import shop.genieus.order.domain.event.OrderCreatedEvent;

@Slf4j
@Component
@RequiredArgsConstructor
public class OrderInternalEventListener {
  private final OrderInternalEventService internalEventService;

  // ------------ BeforeCommit --------
  @TransactionalEventListener(phase = TransactionPhase.BEFORE_COMMIT)
  public void onOrderCreatedBeforeCommit(OrderCreatedEvent event) {
    internalEventService.onOrderCreatedBeforeCommit(event);
  }

  @TransactionalEventListener(phase = TransactionPhase.BEFORE_COMMIT)
  public void onOrderCanceledBeforeCommit(OrderCanceledEvent event) {
    internalEventService.onOrderCanceledBeforeCommit(event);
  }

  @TransactionalEventListener(phase = TransactionPhase.BEFORE_COMMIT)
  public void onPaymentRequestedBeforeCommit(OrderPaymentRequestedEvent event) {
    internalEventService.onPaymentRequestedBeforeCommit(event);
  }

  // ------------ AfterCommit --------
  @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
  public void onOrderCanceledAfterCommit(OrderCanceledEvent event) {
    internalEventService.onOrderCanceledAfterCommit(event);
  }

  @Observed(name = "order.expire", contextualName = "Expire Order")
  @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
  public void onOrderExpiredAfterCommit(OrderExpiredEvent event) {
    internalEventService.onOrderExpiredAfterCommit(event);
  }

  @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
  public void onOrderCompletedAfterCommit(OrderCompletedEvent event) {
    internalEventService.onOrderCompletedAfterCommit(event);
  }

  // ------------ AfterRollback --------
  @EventListener(classes = OrderCreationFailedEvent.class)
  public void onOrderCreationFailedAfterRollback(OrderCreationFailedEvent event) {
    internalEventService.onOrderCreationFailedAfterRollback(event);
  }

  @TransactionalEventListener(phase = TransactionPhase.AFTER_ROLLBACK)
  public void onCouponUsedAfterRollback(CouponRestoredEvent event) {
    internalEventService.onCouponRestoredAfterRollback(event);
  }
}
