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
public class CouponEndDate {

  @Column(name = "coupon_end_date")
  private LocalDateTime value;

  private CouponEndDate(LocalDateTime value) {
    if (value.isBefore(LocalDateTime.now())) {
      // todo. throw exception
    }
    this.value = value;
  }
}
