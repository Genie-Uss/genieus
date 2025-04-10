package shop.genieus.order.domain.model.assembler;

import java.time.LocalDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class CreateOrderAssembler {
  private final Long userId;
  private final List<OrderProductAssembler> orderProducts;
  private LocalDateTime orderedAt;
  private Integer totalProductPrice;
  private Integer PromotionDiscountAmount;

  public void applyOrderedAt(LocalDateTime orderedAt) {
    this.orderedAt = orderedAt;
  }

  public void applyTotalProductPrice(Integer totalProductPrice) {
    this.totalProductPrice = totalProductPrice;
  }

  public void applyPromotionDiscountAmount(Integer promotionDiscountAmount) {
    this.PromotionDiscountAmount = promotionDiscountAmount;
  }
}
