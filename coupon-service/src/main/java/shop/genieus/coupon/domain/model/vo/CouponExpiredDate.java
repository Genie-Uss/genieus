package shop.genieus.coupon.domain.model.vo;

import com.fasterxml.jackson.annotation.JsonCreator;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class CouponExpiredDate {

  @Column(name = "coupon_expired_date", nullable = false)
  private LocalDateTime value;

  @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
  public CouponExpiredDate(LocalDateTime value) {
    this.value = value;
  }

  public boolean validateExpiredDate(LocalDateTime startDate) {
    if (this.value.isBefore(startDate)) { // 발급 만료일자는 시작일자보다 미래
      return false;
    }
    return true;
  }
}
