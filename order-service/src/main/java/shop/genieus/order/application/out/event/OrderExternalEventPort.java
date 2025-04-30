package shop.genieus.order.application.out.event;

import com.genieus.common.event.order.CouponRestoredEvent;
import com.genieus.common.event.order.OrderCanceledEvent;
import com.genieus.common.event.order.OrderCompletedEvent;
import com.genieus.common.event.order.OrderCreationFailedEvent;
import com.genieus.common.event.order.OrderExpiredEvent;
import com.genieus.common.event.payment.PaymentCompletedEvent;
import shop.genieus.order.domain.event.OrderCreatedEvent;

public interface OrderExternalEventPort {
  void sendOrderCanceledEvent(OrderCanceledEvent event);

  void sendOrderCreatedEvent(OrderCreatedEvent event);

  void sendOrderExpiredEvent(OrderExpiredEvent event);

  void sendOrderCompletedEvent(OrderCompletedEvent event);

  void sendPaymentCompletedEvent(PaymentCompletedEvent event);

  void sendOrderCreationFailedEvent(OrderCreationFailedEvent event);

  void sendCouponRestoredEvent(CouponRestoredEvent event);
}
