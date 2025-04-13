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

  private static final int MIN_PRICE = 100;
  private static final int MAX_PRICE = 100_000_000;

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
    if (value == null) {
      throw new IllegalArgumentException("가격은 필수로 입력해야합니다.");
    }

    if (value < MIN_PRICE || value > MAX_PRICE) {
      throw new IllegalArgumentException(
          String.format("가격은 %d원 이상 %d원 이하만 가능합니다.", MIN_PRICE, MAX_PRICE));
    }
  }
}
