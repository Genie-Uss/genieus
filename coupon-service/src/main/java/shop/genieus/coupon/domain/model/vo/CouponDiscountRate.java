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

  private CouponDiscountRate(Integer value) {
    if (value < 1 || value > 99) {
      // todo. throw exception
    }
    this.value = value;
  }
}
