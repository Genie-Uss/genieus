package shop.genieus.coupon.infrastructure.persistence.repository;

import java.util.List;

import shop.genieus.coupon.domain.model.entity.CouponUser;

public interface CouponUserJpaCustom {
  void bulkInsert(List<CouponUser> entities);
}
