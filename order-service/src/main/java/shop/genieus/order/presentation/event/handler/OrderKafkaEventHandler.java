package shop.genieus.order.presentation.event.handler;

import com.genieus.common.event.DomainEvent;
import com.genieus.common.event.payment.PaymentCompletedEvent;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import shop.genieus.order.application.in.command.OrderCommandService;
import shop.genieus.order.application.in.command.dto.CancelOrderCommand;
import shop.genieus.order.application.in.command.dto.CompleteOrderCommand;
import shop.genieus.order.domain.event.OrderCancelTriggerEvent;
import shop.genieus.order.domain.event.OrderCreatedEvent;

@Slf4j
@Component
@RequiredArgsConstructor
public class OrderKafkaEventHandler {
  private final OrderCommandService orderCommandService;

  private final Map<String, Class<? extends DomainEvent>> eventTypeMap =
      Map.of(
          "com.genieus.common.event.order.PaymentCompletedEvent",
          PaymentCompletedEvent.class,
          "shop.genieus.order.domain.event.OrderCreatedEvent",
          OrderCreatedEvent.class,
          "shop.genieus.order.domain.event.OrderCancelTriggerEvent",
          OrderCancelTriggerEvent.class);

  /**
   * 주어진 이벤트 타입 문자열에 해당하는 DomainEvent 클래스를 반환합니다.
   *
   * @param eventType 이벤트 타입을 나타내는 문자열
   * @return 이벤트 타입에 매핑된 DomainEvent 클래스, 매핑이 없으면 null 반환
   */
  public Class<? extends DomainEvent> resolve(String eventType) {
    return eventTypeMap.get(eventType);
  }

  /**
   * 주문 관련 도메인 이벤트를 처리합니다.
   *
   * 결제 완료, 주문 생성, 주문 취소 트리거 이벤트를 구분하여 각각의 비즈니스 로직을 실행합니다.
   * 지원하지 않는 이벤트 타입의 경우 경고 로그를 남깁니다.
   *
   * @param domainEvent 처리할 도메인 이벤트
   */
  public void handle(DomainEvent domainEvent) {
    if (domainEvent instanceof PaymentCompletedEvent event) {
      log.info("[OrderKafkaEventHandler] 결제 완료 이벤트 수신: {}", event);
      CompleteOrderCommand command = new CompleteOrderCommand(event.orderId());
      orderCommandService.completeOrder(command);
      return;
    }
    if (domainEvent instanceof OrderCreatedEvent event) {
      log.info("[OrderKafkaEventHandler] 주문 생성 이벤트 수신: {}", event);
      // todo 조회모델 생성
      return;
    }
    if (domainEvent instanceof OrderCancelTriggerEvent event) {
      log.info("[OrderKafkaEventHandler] 주문 취스 트리거 이벤트 수신: {}", event);
      CancelOrderCommand command = new CancelOrderCommand(-1L, event.orderId());
      orderCommandService.cancelOrderBySystem(command);
      return;
    }
    log.warn("[OrderKafkaEventHandler] 처리할 수 없는 이벤트 타입입니다: {}", domainEvent.getClass().getName());
  }
}
