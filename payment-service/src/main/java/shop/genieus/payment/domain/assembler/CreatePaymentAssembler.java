package shop.genieus.payment.domain.assembler;

import shop.genieus.payment.domain.model.vo.PaymentMethod;

public record CreatePaymentAssembler(
    Long userId,
    Long orderId,
    Integer paymentPrice
    // paymentMethod 는 pay() 에서 받기
) {}
