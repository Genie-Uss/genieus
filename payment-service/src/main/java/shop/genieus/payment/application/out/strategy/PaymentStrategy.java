package shop.genieus.payment.application.out.strategy;

import shop.genieus.payment.domain.model.entity.Payment;

public interface PaymentStrategy {

    PaymentProcessorResult process(Payment payment);
}
