package shop.genieus.order.domain.model.vo;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;

@Getter
@Embeddable
@EqualsAndHashCode
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Quantity {

  @Column(name = "quantity", nullable = false)
  @Comment("수량")
  private Integer quantity;

  private Quantity(Integer quantity) {
    validate(quantity);
    this.quantity = quantity;
  }

  public static Quantity of(Integer quantity) {
    return new Quantity(quantity);
  }

  private void validate(Integer quantity) {
    if (quantity == null) {
      throw new IllegalArgumentException("수량은 필수입니다.");
    }
    if (quantity <= 0) {
      throw new IllegalArgumentException("수량은 0보다 커야 합니다.");
    }
  }
}
