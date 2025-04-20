package shop.genieus.order.presentation.event.external;

import com.genieus.common.event.util.EventRouter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class OrderKafkaListener {

  private final EventRouter eventRouter;

  @KafkaListener(
      topics = {"${spring.kafka.consumer.topic.payment}", "${spring.kafka.consumer.topic.order}"})
  public void consume(@Header(KafkaHeaders.RECEIVED_TOPIC) String topic, @Payload String payload) {
    eventRouter.route(topic, payload);
  }
}
