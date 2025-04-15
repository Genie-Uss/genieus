package shop.genieus.order.global.config;

import static org.apache.kafka.clients.producer.ProducerConfig.*;

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

  @Value("${spring.kafka.bootstrap-servers}")
  private String bootstrapServers;

  /**
   * Kafka 프로듀서를 위한 ProducerFactory 빈을 생성합니다.
   *
   * Kafka 메시지 키는 문자열로, 값은 JSON으로 직렬화된 EventEnvelope 객체로 처리됩니다.
   *
   * @return Kafka 메시지 전송에 사용할 ProducerFactory 인스턴스
   */
  @Bean
  public ProducerFactory<String, EventEnvelope<? extends DomainEvent>> producerFactory() {
    Map<String, Object> props = new HashMap<>();
    props.put(BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
    props.put(KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
    props.put(VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
    return new DefaultKafkaProducerFactory<>(props);
  }

  /**
   * Kafka 메시지를 전송하기 위한 KafkaTemplate 빈을 생성합니다.
   *
   * 이 템플릿은 문자열 키와 JSON으로 직렬화된 EventEnvelope<DomainEvent> 값을 사용하여 Kafka로 메시지를 전송할 수 있도록 지원합니다.
   *
   * @return Kafka 메시지 전송에 사용되는 KafkaTemplate 인스턴스
   */
  @Bean
  public KafkaTemplate<String, EventEnvelope<? extends DomainEvent>> kafkaTemplate() {
    return new KafkaTemplate<>((producerFactory()));
  }
}
