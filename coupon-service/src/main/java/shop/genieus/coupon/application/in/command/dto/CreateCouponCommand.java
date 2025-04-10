package shop.genieus.coupon.application.in.command.dto;

import shop.genieus.coupon.domain.model.entity.Coupon;
import shop.genieus.coupon.domain.model.vo.CouponDiscountRate;
import shop.genieus.coupon.domain.model.vo.CouponEndDate;
import shop.genieus.coupon.domain.model.vo.CouponExpiredDate;
import shop.genieus.coupon.domain.model.vo.CouponStartDate;

public record CreateCouponCommand(
    String couponName,
    CouponDiscountRate couponDiscountRate,
    Integer couponQuantity,
    CouponStartDate couponStartDate,
    CouponEndDate couponEndDate,
    CouponExpiredDate couponExpiredDate,
    Integer couponMaxPrice) {
  public static Coupon toEntity(CreateCouponCommand command) {
    return Coupon.create(
        command.couponName(),
        command.couponDiscountRate(),
        command.couponQuantity(),
        command.couponStartDate(),
        command.couponEndDate(),
        command.couponExpiredDate(),
        command.couponMaxPrice());
  }
}
