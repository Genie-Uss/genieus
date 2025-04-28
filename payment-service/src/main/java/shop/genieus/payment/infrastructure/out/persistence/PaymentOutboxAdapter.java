package shop.genieus.payment.infrastructure.out.persistence;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.genieus.common.event.payment.PaymentCompletedEvent;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import shop.genieus.payment.application.out.persistence.PaymentOutboxPort;
import shop.genieus.payment.global.event.PaymentEvent;
import shop.genieus.payment.global.exception.PaymentErrorCode;
import shop.genieus.payment.global.exception.PaymentException;
import shop.genieus.payment.global.exception.PaymentJsonMappingException;
import shop.genieus.payment.global.exception.PaymentJsonParsingException;
import shop.genieus.payment.infrastructure.out.entity.Outbox;
import shop.genieus.payment.infrastructure.out.repository.OutboxJpaRepository;

@Slf4j
@Component
@RequiredArgsConstructor
public class PaymentOutboxAdapter implements PaymentOutboxPort {

  private final ObjectMapper objectMapper;
  private final OutboxJpaRepository outboxJpaRepository;

  @Override
  public void save(PaymentEvent paymentEvent) {
    PaymentCompletedEvent paymentCompletedEvent = getPaymentCompletedEvent(paymentEvent);

    Outbox outbox = createOutbox(paymentCompletedEvent);
    outboxJpaRepository.save(outbox);
  }

  @Override
  public List<Outbox> findEventsNotPublished() {
    return outboxJpaRepository.findAllByIsPublishedFalse();
  }

  private PaymentCompletedEvent getPaymentCompletedEvent(PaymentEvent paymentEvent) {
    try {
      Long orderId = (Long) paymentEvent.getContext().get(0);
      return new PaymentCompletedEvent(orderId);

    } catch (RuntimeException e) {
      log.warn("CompletedPaymentResult 가 비어있음");
      throw new PaymentException(PaymentErrorCode.COMPLETED_PAYMENT_RESULT_EMPTY);
    }
  }

  private Outbox createOutbox(PaymentCompletedEvent paymentCompletedEvent) {
    try {
      return Outbox.builder().event(objectMapper.writeValueAsString(paymentCompletedEvent)).build();

    } catch (InvalidFormatException e) {
      throw new PaymentJsonMappingException(e);

    } catch (JsonParseException jpe) {
      throw new PaymentJsonParsingException(jpe);

    } catch (JsonProcessingException e) {
      throw new PaymentException(PaymentErrorCode.JSON_PROCESSING_FAILED);
    }
  }
}
