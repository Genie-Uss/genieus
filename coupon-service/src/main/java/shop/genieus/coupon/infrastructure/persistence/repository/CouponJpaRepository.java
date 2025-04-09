package shop.genieus.coupon.infrastructure.persistence.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import shop.genieus.coupon.domain.model.entity.Coupon;

public interface CouponJpaRepository extends JpaRepository<Coupon, Long> {}
