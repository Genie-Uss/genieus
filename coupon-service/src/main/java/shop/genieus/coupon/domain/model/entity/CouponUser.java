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
import lombok.NoArgsConstructor;
import shop.genieus.coupon.domain.model.vo.CouponUseStatus;

@Entity
@Table(name = "m_coupon_user")
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CouponUser {

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

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "coupon_id")
  private Coupon coupon;
}
