package shop.genieus.order.application.in.scheduler;

import java.util.Set;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import shop.genieus.order.application.out.event.OrderEventSendPort;
import shop.genieus.order.application.out.persistence.OrderDelayQueuePort;
import shop.genieus.order.application.out.util.OrderTimePort;
import shop.genieus.order.domain.event.OrderCancelTriggerEvent;

@Slf4j
@Component
@RequiredArgsConstructor
public class OrderScheduler {
  private final OrderTimePort orderTimePort;
  private final OrderEventSendPort orderEventSendPort;
  private final OrderDelayQueuePort orderDelayQueuePort;

  @Scheduled(fixedDelayString = "${order.scheduler.poll-interval:30000}")
  public void pollExpiredMessages() {
    long epochSecond = orderTimePort.getEpochSecond();
    Set<Long> orderIds = orderDelayQueuePort.findExpiredEvents(epochSecond);
    orderIds.forEach(this::handleOrderTrigger);
  }

  private void handleOrderTrigger(Long orderId) {
    try {
      OrderCancelTriggerEvent event = OrderCancelTriggerEvent.of(orderId);
      orderEventSendPort.sendOrderCancelTriggerEvent(event);
      log.info("[handleOrderTrigger] 주문취소 트리거 이벤트 전송: {}", event);
      orderDelayQueuePort.delete(orderId);
    } catch (Exception e) {
      log.error(
          "[handleOrderTrigger] 메시지 처리 중 예외 발생: orderId={}, error={}", orderId, e.getMessage(), e);
      // TODO: DLQ 및 재시도 로직 구현
      // 임시 재시도 로직 - 지연 큐에서 삭제하지 않으면 다음 스케줄링에서 다시 시도됨
    }
  }
}
