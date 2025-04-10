package shop.genieus.order.presentation.rest.dto.request;

import com.genieus.common.auth.model.Passport;
import jakarta.validation.constraints.NotBlank;
import shop.genieus.order.application.in.command.dto.PaymentCommand;

public record PaymentRequest(
    Long couponId, @NotBlank(message = "결제수단은 필수입니다.") String paymentMethod) {
  public PaymentCommand toCommand(Passport passport, Long orderId) {
    return PaymentCommand.builder()
        .userId(passport.getUserId())
        .role(passport.getRole())
        .orderId(orderId)
        .couponId(couponId)
        .paymentMethod(paymentMethod)
        .build();
  }
}
