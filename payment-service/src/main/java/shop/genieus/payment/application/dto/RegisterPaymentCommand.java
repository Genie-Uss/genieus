package shop.genieus.payment.application.dto;

public record RegisterPaymentCommand(
        String paymentKey, // 일단 검증용
        Long orderId,
        Long amount // 일단 검증용
) {}
