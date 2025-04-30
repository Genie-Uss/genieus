package shop.genieus.coupon.presentation.event.consumer;

import static io.micrometer.tracing.Tracer.*;

import com.genieus.common.event.util.EventRouter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@Profile("dev")
@RequiredArgsConstructor
public class CouponKafkaConsumer {

  private final EventRouter eventRouter;

  @KafkaListener(topics = "${spring.kafka.consumer.topic.order}")
  public void consume(@Header(KafkaHeaders.RECEIVED_TOPIC) String topic, @Payload String payload) {
    eventRouter.route(topic, payload);
  }
}
