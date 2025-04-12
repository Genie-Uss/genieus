package shop.genieus.payment.application.out.persistence;

import shop.genieus.payment.domain.model.entity.Payment;

public interface PaymentCommandPort {

    Payment create(Payment payment);
}
