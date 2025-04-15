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

  /**
   * 만료된 주문 이벤트를 주기적으로 조회하여 처리합니다.
   *
   * 30초마다 실행되어 현재 시각 기준으로 만료된 주문 ID를 조회하고, 각 주문에 대해 취소 트리거 처리를 수행합니다.
   */
  @Scheduled(fixedDelay = 30000)
  public void pollExpiredMessages() {
    long epochSecond = orderTimePort.getEpochSecond();
    Set<Long> orderIds = orderDelayQueuePort.findExpiredEvents(epochSecond);
    orderIds.forEach(this::handleOrderTrigger);
  }

  /**
   * 지정된 주문 ID에 대해 주문 취소 트리거 이벤트를 생성하고 전송한 후, 해당 주문을 지연 큐에서 삭제합니다.
   *
   * 예외 발생 시 오류 로그를 남깁니다.
   *
   * @param orderId 주문 ID
   */
  private void handleOrderTrigger(Long orderId) {
    try {
      OrderCancelTriggerEvent event = OrderCancelTriggerEvent.of(orderId);
      orderEventSendPort.sendOrderCancelTriggerEvent(event);
      log.info("[handleOrderTrigger] 주문취소 트리거 이벤트 전송: {}", event);
      orderDelayQueuePort.delete(orderId);
    } catch (Exception e) {
      log.error("[handleOrderTrigger] 메시지 처리 중 예외 발생: {}", orderId);
      // todo DQL 및 재시도 로직 구현
    }
  }
}
