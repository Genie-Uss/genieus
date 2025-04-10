package shop.genieus.coupon.application.out.persistence;

import shop.genieus.coupon.domain.model.entity.Coupon;

public interface CouponCommandPort {
  Coupon createCoupon(Coupon request);
}
