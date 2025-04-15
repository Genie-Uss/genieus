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

  public Class<? extends DomainEvent> resolve(String eventType) {
    return eventTypeMap.get(eventType);
  }

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
