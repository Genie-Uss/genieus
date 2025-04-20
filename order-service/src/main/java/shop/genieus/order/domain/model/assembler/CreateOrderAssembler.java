package shop.genieus.order.domain.model.assembler;

import java.time.LocalDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import shop.genieus.order.domain.model.entity.OrderProduct;

@Getter
@Builder
@AllArgsConstructor
public class CreateOrderAssembler {
  private final Long userId;
  private List<OrderProduct> orderProducts;
  private LocalDateTime orderedAt;
  private LocalDateTime orderDeadlineAt;
  private Integer totalProductPrice;
  private Integer promotionDiscountAmount;

  public void applyOrderProducts(List<OrderProduct> orderProducts) {
    this.orderProducts = orderProducts;
  }

  public void applyOrderPeriod(LocalDateTime orderedAt, LocalDateTime orderDeadlineAt) {
    this.orderedAt = orderedAt;
    this.orderDeadlineAt = orderDeadlineAt;
  }

  public void applyTotalProductPrice(Integer totalProductPrice) {
    this.totalProductPrice = totalProductPrice;
  }

  public void applyPromotionDiscountAmount(Integer promotionDiscountAmount) {
    this.promotionDiscountAmount = promotionDiscountAmount;
  }
}
