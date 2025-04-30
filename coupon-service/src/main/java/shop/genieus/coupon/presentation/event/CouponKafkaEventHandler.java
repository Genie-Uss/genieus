package shop.genieus.coupon.presentation.event;

import com.genieus.common.event.DeadLetterEnvelope;
import com.genieus.common.event.EventEnvelope;
import com.genieus.common.event.annotation.EventTypeMapping;
import com.genieus.common.event.annotation.FallbackMapping;
import com.genieus.common.event.order.CouponRestoredEvent;
import com.genieus.common.event.order.OrderCanceledEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import shop.genieus.coupon.application.in.command.CouponCommandService;

@Slf4j
@Component
@Profile("dev")
@RequiredArgsConstructor
public class CouponKafkaEventHandler {
  private final CouponCommandService commandService;

  @EventTypeMapping(topic = "${spring.kafka.consumer.topic.order}")
  public void handleOrderCanceled(OrderCanceledEvent event) {
    log.info("[handleOrderCanceled] 주문 취소 이벤트 수신 : {}", event);
    Long couponId = event.couponId();
    Long userId = event.userId();
    log.info("couponId : {}", couponId);
    if (couponId == null) {
      log.info("쿠폰을 사용하지 않은 주문입니다.");
      return;
    }
    commandService.cancelCoupon(couponId, userId);
  }

  @EventTypeMapping(topic = "${spring.kafka.consumer.topic.order}")
  public void handleCouponRestored(CouponRestoredEvent event) {
    log.info("[handleCouponRestored] 쿠폰 복구 이벤트 수신 : {}", event);
    commandService.cancelCoupon(event.couponId(), event.userId());
  }

  @FallbackMapping(topic = "order-events", eventType = "OrderCanceledEvent")
  public void handleOrderCanceledFallback(
      EventEnvelope<OrderCanceledEvent> envelope, Throwable ex) {
    log.error("[주문 취소 이벤트 처리 실패 - fallback] envelope:{}, error:{} ", envelope, ex.getMessage());
    DeadLetterEnvelope<OrderCanceledEvent> deadLetterEnvelope =
        DeadLetterEnvelope.from(envelope, ex.getMessage());
  }
}
