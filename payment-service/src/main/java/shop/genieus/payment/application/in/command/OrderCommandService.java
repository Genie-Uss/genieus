package shop.genieus.payment.application.in.command;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import shop.genieus.payment.application.in.event.PaymentEventHandler;
import shop.genieus.payment.domain.model.entity.Payment;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderCommandService implements PaymentEventHandler {

    private final PaymentCommandService paymentCommandService;

    @Transactional
    public void handle(Long orderId) {
        log.warn("[이벤트 수신 - 주문 번호]: {}", orderId);
        Payment payment = paymentCommandService.cancel(orderId);
        log.info("[주문 상태]: {}", payment.getPaymentId());
        log.info("[주문 상태]: {}", payment.getPaymentStatus());
    }
}
