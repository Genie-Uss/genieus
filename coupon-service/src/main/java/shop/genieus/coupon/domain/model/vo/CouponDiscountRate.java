package shop.genieus.coupon.domain.model.vo;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class CouponDiscountRate {

  @Column(name = "coupon_discount_rate", nullable = false)
  private Integer value;

  public CouponDiscountRate(Integer value) {
    if (value < 1 || value > 99) {
      throw new IllegalArgumentException("쿠폰 할인율은 1 ~ 99 사이에서 입력할 수 있습니다.");
    }
    this.value = value;
  }
}
