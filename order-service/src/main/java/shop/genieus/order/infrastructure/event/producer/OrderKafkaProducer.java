package shop.genieus.order.infrastructure.event.producer;

import com.genieus.common.event.DomainEvent;
import com.genieus.common.event.EventEnvelope;
import java.util.concurrent.CompletableFuture;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class OrderKafkaProducer {

  private final KafkaTemplate<String, EventEnvelope<? extends DomainEvent>> kafkaTemplate;

  @Value("${spring.kafka.template.default-topic}")
  private String orderTopic;

  public void publish(String key, EventEnvelope<? extends DomainEvent> eventEnvelop) {
    CompletableFuture<SendResult<String, EventEnvelope<? extends DomainEvent>>> result =
        kafkaTemplate.send(orderTopic, key, eventEnvelop);
    log.info("[publish] key: {}, eventType: {}", key, eventEnvelop.getEventType());

    result.whenComplete(
        (sendResult, exception) -> {
          if (exception != null) {
            log.error(
                "[publish] 메시지 전송 실패: key={}, eventType={}, error={}",
                key,
                eventEnvelop.getEventType(),
                exception.getMessage());
            // TODO: 에러 핸들링 전략 구현 (재시도, DLQ 등)
          } else {
            log.debug(
                "[publish] 메시지 전송 성공: key={}, topic={}, partition={}, offset={}",
                key,
                sendResult.getRecordMetadata().topic(),
                sendResult.getRecordMetadata().partition(),
                sendResult.getRecordMetadata().offset());
          }
        });
  }
}
