package shop.genieus.payment.infrastructure.out.event;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.stereotype.Component;
import shop.genieus.payment.application.out.cache.PaymentCachePort;
import shop.genieus.payment.domain.model.entity.Payment;

@Slf4j
@Component
@RequiredArgsConstructor
public class PaymentCacheAdapter implements PaymentCachePort {

  @Override
  @CachePut(
      key = "'orderId:' + #payment.getOrderId()",
      value = "payment"
  )
  public Payment putPaymentCache(Payment payment) {
    log.info("[결제 객체 캐시 생성] 주문 번호: {}", payment.getOrderId());
    return payment;
  }

  @Override
  @CacheEvict(
      key = "'orderId:' + #orderId",
      value = "payment"
  )
  public void removePaymentCache(Long orderId) {
    log.info("[결제 객체 캐시 삭제] 주문 번호: {}", orderId);
  }
}
