package shop.genieus.payment.domain.assembler;

public record CreatePaymentAssembler(
    Long userId,
    Long orderId,
    Integer paymentPrice
) {}
