package shop.genieus.payment.presentation.rest.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import shop.genieus.payment.application.dto.CreatePaymentCommand;

public record CreatePaymentRequest(
        @JsonProperty("userId") Long userId,
        @JsonProperty("orderId") Long orderId,
        @JsonProperty("amount") Integer amount
) {
    public static CreatePaymentCommand toCommand(CreatePaymentRequest createPaymentRequest) {
        return new CreatePaymentCommand(
                createPaymentRequest.userId(),
                createPaymentRequest.orderId(),
                createPaymentRequest.amount()
        );
    }
}
