package shop.genieus.order.presentation.event.consumer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.genieus.common.event.DomainEvent;
import com.genieus.common.event.EventEnvelope;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import shop.genieus.order.presentation.event.handler.OrderKafkaEventHandler;

@Slf4j
@Component
@RequiredArgsConstructor
public class OrderKafkaConsumer {
  private final ObjectMapper objectMapper;
  private final OrderKafkaEventHandler orderEventHandler;

  @KafkaListener(topics = "${spring.kafka.consumer.topic.payment}")
  public void consumePayment(String message) {
    try {
      String eventType = extractEventType(message);
      Class<? extends DomainEvent> eventClass = orderEventHandler.resolve(eventType);
      if (eventClass == null) {
        log.info("[consumePayment] 처리 대상이 아닌 이벤트 타입입니다: {}", eventType);
        return;
      }
      EventEnvelope<? extends DomainEvent> envelope = deserializeEnvelope(message, eventClass);
      orderEventHandler.handle(envelope.getEvent());
      log.info("[OrderKafkaConsumer] 결제 이벤트 처리완료 : {}", envelope);

    } catch (Exception e) {
      log.error("[consumePayment] 이벤트 처리 실패", e);
    }
  }

  @KafkaListener(topics = "${spring.kafka.consumer.topic.order}")
  public void consumeOrder(String message) {
    try {
      String eventType = extractEventType(message);
      Class<? extends DomainEvent> eventClass = orderEventHandler.resolve(eventType);
      if (eventClass == null) {
        log.info("[consumeOrder] 처리 대상이 아닌 이벤트 타입입니다: {}", eventType);
        return;
      }
      EventEnvelope<? extends DomainEvent> envelope = deserializeEnvelope(message, eventClass);
      orderEventHandler.handle(envelope.getEvent());
      log.info("[consumeOrder] 주문 이벤트 처리완료: {}", envelope);
    } catch (Exception e) {
      log.error("[consumeOrder] 이벤트 처리 실패", e);
    }
  }

  private String extractEventType(String message) {
    try {
      JsonNode root = objectMapper.readTree(message);
      return Optional.ofNullable(root.get("eventType"))
          .map(JsonNode::asText)
          .orElseThrow(() -> new IllegalArgumentException("이벤트 메시지에 'eventType' 필드가 존재하지 않습니다."));
    } catch (JsonProcessingException e) {
      throw new IllegalArgumentException("이벤트 메시지의 JSON 형식이 잘못되었습니다.");
    }
  }

  private EventEnvelope<? extends DomainEvent> deserializeEnvelope(
      String message, Class<? extends DomainEvent> eventClass) {
    try {
      JavaType envelopeType =
          objectMapper.getTypeFactory().constructParametricType(EventEnvelope.class, eventClass);
      return objectMapper.readValue(message, envelopeType);
    } catch (JsonProcessingException e) {
      throw new IllegalArgumentException("이벤트 메시지를 EventEnvelope로 역직렬화하는 데 실패했습니다.");
    }
  }
}
