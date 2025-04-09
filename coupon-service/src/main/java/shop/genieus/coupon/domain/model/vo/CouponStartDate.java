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
public class CouponStartDate {

  @Column(name = "coupon_start_date", nullable = false)
  private LocalDateTime value;

  @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
  public CouponStartDate(LocalDateTime value) {
    this.value = value;
  }
}
