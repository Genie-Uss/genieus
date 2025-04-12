package shop.genieus.payment.presentation.rest.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import shop.genieus.payment.application.dto.ProcessPaymentCommand;

public record ProcessPaymentRequest(
        @JsonProperty("orderId") Long orderId,
        @JsonProperty("paymentMethod") String paymentMethod
) {
    public static ProcessPaymentCommand toCommand(ProcessPaymentRequest processPaymentRequest) {
        return new ProcessPaymentCommand(processPaymentRequest.orderId, processPaymentRequest.paymentMethod);
    }
}
