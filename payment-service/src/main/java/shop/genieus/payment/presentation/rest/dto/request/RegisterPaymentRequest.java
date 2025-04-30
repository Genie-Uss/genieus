package shop.genieus.payment.presentation.rest.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import shop.genieus.payment.application.in.dto.RegisterPaymentCommand;

public record RegisterPaymentRequest(
        @JsonProperty("paymentKey") String paymentKey,
        @JsonProperty("orderId") String orderId, // Toss 생성 orderId + "-" + 우리 서버에서 관리하는 Long orderId
        @JsonProperty("amount") Long amount
) {
    public static RegisterPaymentCommand toCommand(RegisterPaymentRequest registerPaymentRequest) {
        String[] parts = registerPaymentRequest.orderId.split("-");
        Long orderId = Long.parseLong(parts[1]);

        return new RegisterPaymentCommand(
                registerPaymentRequest.paymentKey(),
                orderId,
                registerPaymentRequest.amount());
    }
}
