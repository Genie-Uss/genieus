package shop.genieus.coupon.application.out.persistence;

import shop.genieus.coupon.domain.model.entity.Coupon;
import shop.genieus.coupon.domain.model.entity.CouponUser;

public interface CouponCommandPort {
  Coupon createCoupon(Coupon request);

  CouponUser validUserCoupon(Long couponId, Long userId);

  Coupon findCoupon(Long couponId);

  CouponUser findCouponUser(Long couponId, Long userId);

  void createCouponUser(Coupon coupon, Long userId);

  void saveInitialStock(Coupon coupon);
}
