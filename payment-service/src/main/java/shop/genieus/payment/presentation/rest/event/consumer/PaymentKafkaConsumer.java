package shop.genieus.payment.presentation.rest.event.consumer;

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
public class PaymentKafkaConsumer {

  private final EventRouter eventRouter;

  @KafkaListener(topics = "order-events", containerFactory = "kafkaListenerContainerFactory")
  public void consume(@Header(KafkaHeaders.RECEIVED_TOPIC) String topic, @Payload String payload) {
    log.info("[consume 시작] payload: {}", payload);
    eventRouter.route(topic, payload);
    log.info("[consume 종료] topic 종료: {}", topic);
  }
}
