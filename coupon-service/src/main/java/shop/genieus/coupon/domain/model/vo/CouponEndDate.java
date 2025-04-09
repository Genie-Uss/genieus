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
    this.value = value;
  }

  public boolean validateEndDate(LocalDateTime startDate) {
    if (this.value.isBefore(startDate)) { // 발급 종료일자는 시작일자와 같거나 미래
      return false;
    }
    return true;
  }
}
