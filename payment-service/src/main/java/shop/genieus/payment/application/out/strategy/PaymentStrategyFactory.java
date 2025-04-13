package shop.genieus.payment.application.out.strategy;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import shop.genieus.payment.domain.model.vo.PaymentMethod;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class PaymentStrategyFactory {

    private final Map<String, PaymentStrategy> strategies;

    public PaymentStrategy getStrategy(PaymentMethod paymentMethod) {
        PaymentStrategy paymentStrategy = strategies.get(paymentMethod.name());

        if (paymentStrategy == null) {
            throw new IllegalArgumentException(paymentMethod + "는 지원하지 않는 결제 수단입니다.");
        }

        return paymentStrategy;
    }
}
