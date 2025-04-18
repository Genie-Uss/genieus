package shop.genieus.coupon.infrastructure.persistence;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import shop.genieus.coupon.application.out.persistence.CouponCommandPort;
import shop.genieus.coupon.domain.model.entity.Coupon;
import shop.genieus.coupon.domain.model.entity.CouponUser;
import shop.genieus.coupon.infrastructure.persistence.repository.CouponJpaRepository;
import shop.genieus.coupon.infrastructure.persistence.repository.CouponUserJpaRepository;
import shop.genieus.coupon.infrastructure.persistence.repository.RedisCouponRepository;

@Component
@RequiredArgsConstructor
public class CouponCommandAdaptor implements CouponCommandPort {
  private final CouponJpaRepository jpaRepository;
  private final CouponUserJpaRepository couponUserJpaRepository;
  private final RedisCouponRepository redisRepository;

  @Override
  public Coupon createCoupon(Coupon request) {
    return jpaRepository.save(request);
  }

  @Override
  public CouponUser validUserCoupon(Long couponId, Long userId) {
    return couponUserJpaRepository.findCouponByCondition(couponId, userId);
  }

  @Override
  public Coupon findCoupon(Long couponId) {
    return jpaRepository.findByCouponIdAndDeletedAtIsNull(couponId);
  }

  @Override
  public void createCouponUser(Coupon coupon, Long userId) {
    redisRepository.createCouponUser(coupon, userId);
  }

  @Override
  public void saveInitialStock(Coupon coupon) {
    redisRepository.saveInitialStock(coupon);
  }
}
