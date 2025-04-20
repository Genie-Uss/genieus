package shop.genieus.payment.infrastructure.in.listener;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import shop.genieus.payment.application.in.event.PaymentEventHandler;

@Slf4j
@Component
@RequiredArgsConstructor
public class KafkaPaymentListener {

    private final ObjectMapper objectMapper;
    private final PaymentEventHandler paymentEventListener;

    @KafkaListener(
            topics = "order",
            groupId = "payment-server",
            containerFactory = "kafkaListenerContainerFactory"

    )
    public void orderCancelEventHandle(String key) {
        try {
            Long orderId = objectMapper.readValue(key, Long.class);
            log.info("[주문 취소 이벤트] 메세지 수신: {}", orderId);
            paymentEventListener.handle(orderId);
            log.info("[주문 취소 이벤트] 결제 취소 완료");
        } catch (IllegalArgumentException e) {
            log.warn("[주문 취소 이벤트] 결제 취소 실패: {}", e.getMessage());
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
