package shop.genieus.payment.application.out.strategy;

import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import shop.genieus.payment.domain.model.vo.PaymentMethod;
import shop.genieus.payment.global.exception.PaymentErrorCode;
import shop.genieus.payment.global.exception.PaymentException;

@Service
@RequiredArgsConstructor
public class PaymentStrategyFactory {

  private final Map<String, PaymentStrategy> strategies;

  public PaymentStrategy getStrategy(PaymentMethod paymentMethod) {
    PaymentStrategy paymentStrategy = strategies.get(paymentMethod.name());

    if (paymentStrategy == null) {
      throw new PaymentException(PaymentErrorCode.INVALID_PAYMENT_METHOD);
    }

    return paymentStrategy;
  }
}
