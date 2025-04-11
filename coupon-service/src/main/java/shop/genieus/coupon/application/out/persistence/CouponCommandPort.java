package shop.genieus.coupon.application.out.persistence;

import shop.genieus.coupon.domain.model.entity.Coupon;
import shop.genieus.coupon.domain.model.entity.CouponUser;

public interface CouponCommandPort {
  Coupon createCoupon(Coupon request);

  CouponUser validUserCoupon(Long couponId, Long userId);

  Coupon findCoupon(Long couponId);
}
