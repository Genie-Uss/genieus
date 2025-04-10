package shop.genieus.payment.domain.assembler;

import shop.genieus.payment.domain.model.vo.PaymentMethod;

public record CreatePaymentAssembler(
    Long userId,
    Long orderId,
    Integer paymentPrice,
    PaymentMethod paymentMethod
) {}
