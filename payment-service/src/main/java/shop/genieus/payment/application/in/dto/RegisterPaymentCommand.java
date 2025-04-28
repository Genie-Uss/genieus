package shop.genieus.payment.application.in.dto;

public record RegisterPaymentCommand(
        String paymentKey,
        Long orderId,
        Long amount
) {}
