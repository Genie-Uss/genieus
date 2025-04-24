package shop.genieus.coupon.domain.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import shop.genieus.coupon.domain.model.vo.CouponUseStatus;
import shop.genieus.coupon.infrastructure.persistence.dto.IssueCouponCommand;

@Entity
@Table(name = "m_coupon_user")
@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CouponUser extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long couponUserId;

  @Column(nullable = false)
  private Long userId;

  @Column(nullable = false)
  private LocalDateTime couponUserIssuedDate;

  @Column(nullable = false)
  private LocalDateTime couponUserExpiredDate;

  private LocalDateTime couponUserUsedDate;

  @Column(nullable = false)
  @Enumerated(EnumType.STRING)
  private CouponUseStatus couponUserStatus;

  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "coupon_id")
  private Coupon coupon;

  public void useCoupon() {
    this.couponUserStatus = CouponUseStatus.USED;
    this.couponUserUsedDate = LocalDateTime.now();
  }

  public void cancelCoupon() {
    validateUsableExpiredDate();
    this.couponUserStatus = CouponUseStatus.AVAILABLE;
    this.couponUserUsedDate = null;
  }

  // 쿠폰 사용 및 취소 전 만료 여부 확인
  public void validateUsableExpiredDate() {
    if (this.getCouponUserExpiredDate().isBefore(LocalDateTime.now())) {
      throw new IllegalArgumentException("사용 기한이 지난 쿠폰입니다.");
    }
  }

  public void validateUsableStatus() {
    if (!this.getCouponUserStatus().equals(CouponUseStatus.AVAILABLE)) {
      throw new IllegalArgumentException("사용 불가능한 쿠폰입니다.");
    }
  }

  public static CouponUser create(IssueCouponCommand dto) {
    return CouponUser.builder()
        .userId(dto.userId())
        .couponUserIssuedDate(dto.issuedDate())
        .couponUserExpiredDate(dto.expiredDate())
        .couponUserStatus(dto.status())
        .build();
  }
}
