package shop.genieus.product.global.config;

import static org.apache.kafka.clients.producer.ProducerConfig.*;

import brave.Tracing;
import brave.kafka.clients.KafkaTracing;
import com.genieus.common.event.DomainEvent;
import com.genieus.common.event.EventEnvelope;
import java.util.HashMap;
import java.util.Map;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonSerializer;

@EnableKafka
@Configuration
public class KafkaProducerConfig {

  private final String bootstrapServers;

  public KafkaProducerConfig(@Value("${spring.kafka.bootstrap-servers}") String bootstrapServers) {
    this.bootstrapServers = bootstrapServers;
  }

  @Bean
  public KafkaTracing kafkaTracing(Tracing tracing) {
    return KafkaTracing.create(tracing);
  }

  @Bean
  public Map<String, Object> producerConfig() {
    Map<String, Object> props = new HashMap<>();
    props.put(BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
    props.put(KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
    props.put(VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
    props.put(RETRIES_CONFIG, 3);
    props.put(ENABLE_IDEMPOTENCE_CONFIG, true);
    props.put(BATCH_SIZE_CONFIG, 16384);
    props.put(LINGER_MS_CONFIG, 5);
    props.put(COMPRESSION_TYPE_CONFIG, "snappy");
    props.put(ACKS_CONFIG, "all");
    return props;
  }

  @Bean
  public ProducerFactory<String, EventEnvelope<? extends DomainEvent>> producerFactory(
      Map<String, Object> producerConfig) {
    return new DefaultKafkaProducerFactory<>(producerConfig);
  }

  @Bean
  public KafkaTemplate<String, EventEnvelope<? extends DomainEvent>> kafkaTemplate(
      ProducerFactory<String, EventEnvelope<? extends DomainEvent>> pf) {
    KafkaTemplate<String, EventEnvelope<? extends DomainEvent>> template = new KafkaTemplate<>(pf);
    template.setObservationEnabled(true);
    return template;
  }
}
