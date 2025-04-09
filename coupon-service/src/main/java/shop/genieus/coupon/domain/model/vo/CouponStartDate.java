package shop.genieus.coupon.domain.model.vo;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class CouponStartDate {

  @Column(name = "coupon_start_date", nullable = false)
  private LocalDateTime value;

  private CouponStartDate(LocalDateTime value) {
    this.value = value;
  }
}
