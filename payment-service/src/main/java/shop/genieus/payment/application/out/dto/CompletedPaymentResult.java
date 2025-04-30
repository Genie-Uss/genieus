package shop.genieus.payment.application.out.dto;

import java.util.List;
import shop.genieus.payment.global.event.PaymentEvent;

public record CompletedPaymentResult(Long orderId) implements PaymentEvent {

  @Override
  public List<Object> getContext() {
    return List.of(orderId);
  }

  public static CompletedPaymentResult of(Long orderId) {
    return new CompletedPaymentResult(orderId);
  }
}
