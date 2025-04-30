package shop.genieus.payment.application.in.dto;

public record ProcessPaymentCommand(
        Long orderId,
        String paymentMethod
) {}
