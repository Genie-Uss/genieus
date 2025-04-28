package shop.genieus.payment.application.in.dto;

import shop.genieus.payment.domain.assembler.CreatePaymentAssembler;

public record CreatePaymentCommand(
        Long userId,
        Long orderId,
        Integer amount
) {
    public CreatePaymentAssembler toAssembler() {
        return new CreatePaymentAssembler(userId, orderId, amount);
    }
}
