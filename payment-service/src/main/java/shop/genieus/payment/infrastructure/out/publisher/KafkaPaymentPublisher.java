package shop.genieus.payment.infrastructure.out.publisher;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
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

    // TODO 봉투에 넣어야 함
    @Override
    public void CreatePaymentEvent(Long orderId) {
        log.info("결제 완료 이벤트 발행");
        try {
            // payment-create? payment-success?
            kafkaTemplate.send("payment-create", objectMapper.writeValueAsString(orderId));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
