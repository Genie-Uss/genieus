package shop.genieus.payment.infrastructure.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import shop.genieus.payment.domain.model.entity.Payment;

import java.util.Optional;

public interface PaymentJpaRepository extends JpaRepository<Payment, Long> {

    Optional<Payment> getPaymentByOrderId(Long orderId);
}
