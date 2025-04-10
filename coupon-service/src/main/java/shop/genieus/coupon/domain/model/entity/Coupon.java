package shop.genieus.coupon.domain.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;
import shop.genieus.coupon.domain.model.vo.CouponDiscountRate;
import shop.genieus.coupon.domain.model.vo.CouponEndDate;
import shop.genieus.coupon.domain.model.vo.CouponExpiredDate;
import shop.genieus.coupon.domain.model.vo.CouponStartDate;
import shop.genieus.coupon.domain.model.vo.CouponStatus;

@Entity
@Builder(access = AccessLevel.PROTECTED)
@Table(name = "m_coupon")
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Coupon extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long couponId;

  @Column(nullable = false)
  private String couponName;

  @Embedded private CouponDiscountRate couponDiscountRate;

  @Column(nullable = false)
  @Enumerated(EnumType.STRING)
  private CouponStatus couponStatus;

  @Column(nullable = false)
  private Integer couponQuantity;

  @Embedded
  @Comment("쿠폰 발급 시작 일자")
  private CouponStartDate couponStartDate;

  @Embedded
  @Comment("쿠폰 발급 종료 일자")
  private CouponEndDate couponEndDate;

  @Embedded
  @Comment("쿠폰 만료 일자")
  private CouponExpiredDate couponExpiredDate;

  @Column(nullable = false)
  private Integer couponMaxPrice;

  public static Coupon create(
      String couponName,
      CouponDiscountRate discountRate,
      Integer quantity,
      CouponStartDate startDate,
      CouponEndDate endDate,
      CouponExpiredDate expiredDate,
      Integer couponMaxPrice) {
    return Coupon.builder()
        .couponName(couponName)
        .couponDiscountRate(discountRate)
        .couponStatus(CouponStatus.READY)
        .couponQuantity(quantity)
        .couponStartDate(startDate)
        .couponEndDate(endDate)
        .couponExpiredDate(expiredDate)
        .couponMaxPrice(couponMaxPrice)
        .build();
  }
}
