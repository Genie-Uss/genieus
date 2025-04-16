package shop.genieus.payment.infrastructure.out.persistence;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import shop.genieus.payment.application.out.persistence.PaymentCommandPort;
import shop.genieus.payment.domain.model.entity.Payment;
import shop.genieus.payment.infrastructure.out.repository.PaymentJpaRepository;

@Repository
@RequiredArgsConstructor
public class PaymentCommandAdapter implements PaymentCommandPort {

    private final PaymentJpaRepository paymentJpaRepository;

    @Override
    public Payment create(Payment payment) {
        return paymentJpaRepository.save(payment);
    }

    @Override
    public Payment findPaymentByOrderId(Long orderId) {
        return paymentJpaRepository.getPaymentByOrderId(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Payment not found"));
    }
}
