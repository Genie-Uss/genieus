package shop.genieus.payment.infrastructure.processor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import shop.genieus.payment.application.out.strategy.PaymentProcessorResult;
import shop.genieus.payment.application.out.strategy.PaymentStrategy;
import shop.genieus.payment.domain.model.entity.Payment;

@Slf4j
@Component("TOSS_PAY")
public class TossPayProcessor implements PaymentStrategy {

    @Override
    public PaymentProcessorResult process(Payment payment) {
        return PaymentProcessorResult.returnRedirect(
                "/api/v1/payments/toss?orderId=" + payment.getOrderId() +
                    "&amount=" + payment.getPaymentPrice().totalPrice()
        );
    }
}
