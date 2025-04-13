package shop.genieus.payment.application.dto;

public record ProcessPaymentCommand(
        Long orderId,
        String paymentMethod
) {}
