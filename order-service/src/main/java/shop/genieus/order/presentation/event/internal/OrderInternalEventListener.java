package shop.genieus.order.presentation.event.internal;

import com.genieus.common.event.order.OrderCanceledEvent;
import com.genieus.common.event.order.OrderExpiredEvent;
import com.genieus.common.event.order.PaymentRequestedEvent;
import com.genieus.common.event.payment.PaymentCompletedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
  public void onPaymentRequestedBeforeCommit(PaymentRequestedEvent event) {
    internalEventService.onPaymentRequestedBeforeCommit(event);
  }

  // ------------ AfterCommit --------
  @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
  public void onOrderCanceledAfterCommit(OrderCanceledEvent event) {
    internalEventService.onOrderCanceledAfterCommit(event);
  }

  @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
  public void onOrderExpiredAfterCommit(OrderExpiredEvent event) {
    internalEventService.onOrderExpiredAfterCommit(event);
  }

  @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
  public void onPaymentCompletedAfterCommit(PaymentCompletedEvent event) {
    internalEventService.onPaymentCompletedAfterCommit(event);
  }
}
