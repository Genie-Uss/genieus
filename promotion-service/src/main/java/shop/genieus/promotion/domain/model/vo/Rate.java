package shop.genieus.promotion.domain.model.vo;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Rate {

  @Column(name = "promotion_product_discount_rate", nullable = false)
  private Integer value;

  private Rate(Integer value) {
    validateRate(value);
    this.value = value;
  }

  public static Rate of(Integer value) {
    return new Rate(value);
  }

  private void validateRate(Integer value) {
    if(value == null) {
      throw new IllegalArgumentException("할인율은 필수로 입력해야 합니다.");
    }

    if(value < 1 || value > 99) {
      throw new IllegalArgumentException("할인율은 1이상 99이하의 정수만 가능합니다.");
    }
  }
}
