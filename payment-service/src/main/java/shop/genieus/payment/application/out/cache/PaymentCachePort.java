package shop.genieus.payment.application.out.cache;

import shop.genieus.payment.domain.model.entity.Payment;

public interface PaymentCachePort {

  Payment putPaymentCache(Payment payment);

  void removePaymentCache(Long orderId);
}
