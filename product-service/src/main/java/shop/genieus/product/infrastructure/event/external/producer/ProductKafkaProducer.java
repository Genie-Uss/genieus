package shop.genieus.product.infrastructure.event.external.producer;

import com.genieus.common.event.DomainEvent;
import com.genieus.common.event.EventEnvelope;
import java.util.concurrent.CompletableFuture;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ProductKafkaProducer {
  private final KafkaTemplate<String, EventEnvelope<? extends DomainEvent>> kafkaTemplate;

  @Value("${spring.kafka.template.default-topic}")
  private String topic;

  public void publish(String key, EventEnvelope<? extends DomainEvent> eventEnvelop) {

    ProducerRecord<String, EventEnvelope<? extends DomainEvent>> record =
        new ProducerRecord<>(topic, key, eventEnvelop);

    CompletableFuture<SendResult<String, EventEnvelope<? extends DomainEvent>>> result =
        kafkaTemplate.send(record);
  }
}
