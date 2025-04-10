package shop.genieus.coupon.infrastructure.persistence;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import shop.genieus.coupon.application.out.persistence.CouponCommandPort;
import shop.genieus.coupon.domain.model.entity.Coupon;
import shop.genieus.coupon.infrastructure.persistence.repository.CouponJpaRepository;

@Component
@RequiredArgsConstructor
public class CouponCommandAdaptor implements CouponCommandPort {
  private final CouponJpaRepository jpaRepository;

  @Override
  public Coupon createCoupon(Coupon request) {
    return jpaRepository.save(request);
  }
}
