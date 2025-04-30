package shop.genieus.payment.presentation.rest.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import shop.genieus.payment.application.in.dto.CreatePaymentCommand;

public record CreatePaymentRequest(
    @JsonProperty("orderId") Long orderId,
    @JsonProperty("amount") Integer amount
) {
  public static CreatePaymentCommand toCommand(CreatePaymentRequest createPaymentRequest, Long userId) {
    return new CreatePaymentCommand(
        userId, createPaymentRequest.orderId(), createPaymentRequest.amount());
  }
}
