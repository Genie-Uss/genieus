package shop.genieus.product.domain.model.vo;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ProductPrice {

  @Column(name = "product_price")
  private Integer value;

  private ProductPrice(Integer value) {
    validate(value);
    this.value = value;
  }

  public static ProductPrice of(Integer value) {
    return new ProductPrice(value);
  }

  private void validate(Integer value) {
    if(value == null) {
      throw new IllegalArgumentException("가격은 필수로 입력해야합니다.");
    }

    if(value < 100 || value > 100000000) {
      throw new IllegalArgumentException("가격은 100원 이상 1억 이하만 가능합니다.");
    }
  }
}
