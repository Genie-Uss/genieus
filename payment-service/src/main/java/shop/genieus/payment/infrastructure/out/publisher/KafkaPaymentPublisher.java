package shop.genieus.payment.infrastructure.out.publisher;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.genieus.common.event.EventEnvelope;
import com.genieus.common.event.payment.PaymentCompletedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import shop.genieus.payment.application.out.event.PaymentEventService;

@Slf4j
@Component
@RequiredArgsConstructor
public class KafkaPaymentPublisher implements PaymentEventService {

  private final KafkaTemplate<String, String> kafkaTemplate;
  private final ObjectMapper objectMapper;

  @Override
  public void createPaymentEvent(Long orderId) {
    log.info("[결제 완료 이벤트 시작] 결제 완료 이벤트 발행 시작");

    var event = new PaymentCompletedEvent(orderId);
    var eventEnvelope = EventEnvelope.create(event);

    try {
      String json = objectMapper.writeValueAsString(eventEnvelope);
      kafkaTemplate.send("payment-events", json);
      log.info("[결제 완료 이벤트 성공] 결제 완료 이벤트 발행 완료: {}", json);
    } catch (JsonProcessingException e) {
      throw new RuntimeException(e);
    }
  }
}
