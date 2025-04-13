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
public class Product {

  @Column(name = "product_id", nullable = false)
  @Comment("상품 식별자")
  private Long productId;

  @Column(name = "product_price", nullable = false)
  @Comment("상품 정상가")
  private Integer productPrice;

  private Product(Long id, Integer price) {
    validate(id, price);
    this.productId = id;
    this.productPrice = price;
  }

  public static Product of(Long productId, Integer productPrice) {
    return new Product(productId, productPrice);
  }

  private void validate(Long productId, Integer productPrice) {
    if (productId == null) {
      throw new IllegalArgumentException("상품은 필수입니다.");
    }
    if (productPrice == null) {
      throw new IllegalArgumentException("상품 가격은 필수입니다.");
    }
    if (productPrice < 0) {
      throw new IllegalArgumentException("상품 가격은 음수일 수 없습니다.");
    }
  }
}
