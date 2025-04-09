package shop.genieus.coupon.presentation.rest.dto.response;

import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import shop.genieus.coupon.domain.model.entity.Coupon;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class CouponResponse {
  private Long couponId;

  private String couponName;

  private Integer couponDiscountRate;

  private String couponStatus;

  private Integer couponQuantity;

  private LocalDateTime couponStartDate;

  private LocalDateTime couponEndDate;

  private LocalDateTime couponExpiredDate;

  private Integer couponMaxPrice;

  public static CouponResponse from(Coupon coupon) {
    return new CouponResponse(
        coupon.getCouponId(),
        coupon.getCouponName(),
        coupon.getCouponDiscountRate().getValue(),
        coupon.getCouponStatus().getDescription(),
        coupon.getCouponQuantity(),
        coupon.getCouponStartDate().getValue(),
        coupon.getCouponEndDate().getValue(),
        coupon.getCouponExpiredDate().getValue(),
        coupon.getCouponMaxPrice());
  }
}
