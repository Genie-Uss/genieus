package shop.genieus.product.domain.model.vo;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Embeddable
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ProductName {

  private static final int MAX_LENGTH = 30;

  @Column(name = "product_name", nullable = false, length = MAX_LENGTH)
  private String value;

  private ProductName(String value) {
    validate(value);
    this.value = value;
  }

  public static ProductName of(String value) {
    return new ProductName(value);
  }

  private void validate(String value) {
    if (value == null || value.isBlank()) {
      throw new IllegalArgumentException("상품 이름은 필수입니다.");
    }

    if (value.length() > MAX_LENGTH) {
      throw new IllegalArgumentException("상품 이름은 %d자 이내여야 합니다.".formatted(MAX_LENGTH));
    }
  }
}
