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

  /**
   * 결제 관련 Kafka 메시지를 수신하여 이벤트 타입에 따라 처리합니다.
   *
   * 메시지에서 이벤트 타입을 추출한 뒤, 해당 타입의 도메인 이벤트로 역직렬화하여 이벤트 핸들러에 위임합니다.
   * 지원하지 않는 이벤트 타입일 경우 처리를 건너뜁니다.
   *
   * @param message Kafka에서 수신한 결제 이벤트 JSON 메시지
   */
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

  /**
   * 주문 관련 Kafka 메시지를 수신하여 이벤트 타입에 따라 적절한 도메인 이벤트로 역직렬화하고 처리합니다.
   *
   * @param message Kafka에서 수신한 주문 이벤트 JSON 메시지
   */
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

  /**
   * JSON 메시지에서 'eventType' 필드 값을 추출합니다.
   *
   * @param message 이벤트 정보를 담고 있는 JSON 문자열
   * @return 'eventType' 필드의 값
   * @throws IllegalArgumentException 'eventType' 필드가 없거나 JSON 형식이 잘못된 경우 발생
   */
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

  /**
   * 주어진 JSON 메시지를 지정된 도메인 이벤트 타입의 EventEnvelope 객체로 역직렬화합니다.
   *
   * @param message 역직렬화할 JSON 문자열
   * @param eventClass EventEnvelope에 포함될 도메인 이벤트의 클래스 타입
   * @return 역직렬화된 EventEnvelope 객체
   * @throws IllegalArgumentException 역직렬화에 실패한 경우 발생
   */
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
