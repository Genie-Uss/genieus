package shop.genieus.order.domain.model.assembler;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class OrderProductAssembler {
  private final Long productId;
  private final Long promotionId;
  private final Integer quantity;
  private Integer promotionDiscountRate;
  private Integer productPrice;

  public void applyDiscountRate(Integer discountRate) {
    this.promotionDiscountRate = discountRate;
  }

  public void applyProductPrice(Integer price) {
    this.productPrice = price;
  }
}
