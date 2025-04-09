package shop.genieus.coupon.domain.model.vo;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CouponExpiredDate {

  @Column(name = "coupon_expired_date", nullable = false)
  private LocalDateTime value;

  private CouponExpiredDate(LocalDateTime value) {
    if (value.isBefore(LocalDateTime.now())) {
      // todo. exception
    }
    this.value = value;
  }
}
