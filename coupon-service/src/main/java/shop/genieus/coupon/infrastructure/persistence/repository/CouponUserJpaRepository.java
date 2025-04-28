package shop.genieus.coupon.infrastructure.persistence.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import shop.genieus.coupon.domain.model.entity.CouponUser;

public interface CouponUserJpaRepository
    extends JpaRepository<CouponUser, Long>, CouponUserJpaCustom {
  @Query(
      "SELECT cu FROM CouponUser cu WHERE cu.coupon.couponId = :couponId "
          + "AND cu.userId = :userId "
          + "AND cu.deletedAt IS NULL")
  CouponUser findCouponByCondition(@Param("couponId") Long couponId, @Param("userId") Long userId);
}
