package shop.genieus.coupon.presentation.event;

import com.genieus.common.event.DeadLetterEnvelope;
import com.genieus.common.event.EventEnvelope;
import com.genieus.common.event.annotation.EventTypeMapping;
import com.genieus.common.event.annotation.FallbackMapping;
import com.genieus.common.event.order.OrderCanceledEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import shop.genieus.coupon.application.in.command.CouponCommandService;

@Slf4j
@Component
@RequiredArgsConstructor
public class CouponKafkaEventHandler {
  private final CouponCommandService commandService;

  @EventTypeMapping(topic = "order-events")
  public void handleOrderCanceled(OrderCanceledEvent event) {
    log.info("[handleOrderCanceled] 주문 취소 이벤트 수신 : {}", event);
    Long couponId = event.couponId();
    Long userId = event.userid();
    log.info("couponId : {}", couponId);
    commandService.cancelCoupon(couponId, userId);
  }

  @FallbackMapping(topic = "order-events", eventType = "OrderCanceledEvent")
  public void handleOrderCanceledFallback(
      EventEnvelope<OrderCanceledEvent> envelope, Throwable ex) {
    log.error("[주문 트리거 이벤트 실패 - fallback] envelope:{}, error:{} ", envelope, ex.getMessage());
    DeadLetterEnvelope<OrderCanceledEvent> deadLetterEnvelope =
        DeadLetterEnvelope.from(envelope, ex.getMessage());
  }
}
