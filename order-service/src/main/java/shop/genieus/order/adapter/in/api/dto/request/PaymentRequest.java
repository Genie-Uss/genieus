package shop.genieus.order.adapter.in.api.dto.request;

import com.genieus.common.auth.model.Passport;
import shop.genieus.order.application.in.command.dto.PaymentCommand;

public record PaymentRequest(Long couponId) {
  public PaymentCommand toCommand(Passport passport, Long orderId) {
    return PaymentCommand.builder()
        .userId(passport.getUserId())
        .orderId(orderId)
        .couponId(couponId)
        .build();
  }
}
