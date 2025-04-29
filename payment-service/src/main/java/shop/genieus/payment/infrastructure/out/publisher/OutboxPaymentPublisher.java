package shop.genieus.payment.infrastructure.out.publisher;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.genieus.common.event.EventEnvelope;
import com.genieus.common.event.payment.PaymentCompletedEvent;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import shop.genieus.payment.global.exception.PaymentJsonMappingException;
import shop.genieus.payment.global.exception.PaymentJsonParsingException;
import shop.genieus.payment.infrastructure.out.entity.Outbox;
import shop.genieus.payment.infrastructure.out.persistence.PaymentOutboxAdapter;

@Slf4j
@Component
@RequiredArgsConstructor
public class OutboxPaymentPublisher {

  private final PaymentOutboxAdapter paymentOutboxAdapter;
  private final KafkaTemplate<String, String> kafkaTemplate;
  private final ObjectMapper objectMapper;

  @Transactional
  @Scheduled(fixedDelay = 5000)
  public void publishCompletedPaymentEvents() {
    List<Outbox> eventsNotPublished = paymentOutboxAdapter.findEventsNotPublished();
    if (eventsNotPublished.isEmpty()) { return; }

    for (Outbox outbox : eventsNotPublished) {
      log.info("[아웃박스 발행 시작] paymentOutboxId: {}", outbox.getPaymentOutboxId() + " " + outbox.getIsPublished());

      log.info("[결제 완료 이벤트 발행 시작]");
      var event = getPaymentCompletedEvent(outbox);
      var eventEnvelope = EventEnvelope.create(event);

      try {
        String json = objectMapper.writeValueAsString(eventEnvelope);
        kafkaTemplate.send("payment-events", json);
        log.info("[결제 완료 이벤트 발행 완료] json: {}", json);

        outbox.markPublished();
        log.info("[아웃박스 발행 완료] paymentOutboxId: {}", outbox.getPaymentOutboxId() + " " + outbox.getIsPublished());

      } catch (JsonProcessingException e) {
        throw new RuntimeException(e);
      }
    }
  }

  private PaymentCompletedEvent getPaymentCompletedEvent(Outbox outbox) {
    try {
      return objectMapper.readValue(outbox.getEvent(), PaymentCompletedEvent.class);

    } catch (InvalidFormatException ife) {
      throw new PaymentJsonMappingException(ife);

    } catch (JsonProcessingException jpe) {
      throw new PaymentJsonParsingException(jpe);
    }
  }
}

