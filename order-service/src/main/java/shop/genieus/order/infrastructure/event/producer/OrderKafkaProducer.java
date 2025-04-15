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

  /**
   * 지정된 키와 이벤트 엔벨로프를 Kafka 토픽에 비동기적으로 발행합니다.
   *
   * @param key 메시지의 파티션을 결정하는 키
   * @param eventEnvelop 발행할 도메인 이벤트를 감싼 엔벨로프 객체
   */
  public void publish(String key, EventEnvelope<? extends DomainEvent> eventEnvelop) {
    CompletableFuture<SendResult<String, EventEnvelope<? extends DomainEvent>>> result =
        kafkaTemplate.send(orderTopic, key, eventEnvelop);

    log.info("[publish] eventType: {}", eventEnvelop.getEventType());
  }
}
