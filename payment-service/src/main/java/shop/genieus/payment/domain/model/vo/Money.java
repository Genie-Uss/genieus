package shop.genieus.payment.domain.model.vo;

import jakarta.persistence.Embeddable;

@Embeddable
public record Money(
        Integer totalPrice
) {

    public Money(Integer totalPrice) {
        validate(totalPrice);
        this.totalPrice = totalPrice;
    }

    private void validate(Integer totalPrice) {
        if (totalPrice < 0) {
            throw new IllegalArgumentException("결제 금액은 음수가 될 수 없습니다.");
        }
    }
}
