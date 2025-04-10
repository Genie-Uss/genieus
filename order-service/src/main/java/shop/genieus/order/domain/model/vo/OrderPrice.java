package shop.genieus.order.domain.model.vo;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Comment;

@Getter
@Embeddable
@EqualsAndHashCode
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class OrderPrice {

  @Column(name = "total_product_price", nullable = false)
  @Comment("총 상품금액")
  private Integer totalProductPrice;

  @Column(name = "total_discount_amount", nullable = false)
  @Comment("총 할인금액")
  private Integer totalDiscountAmount;

  @Column(name = "promotion_discount_amount", nullable = false)
  @Comment("프로모션 할인금액")
  private Integer promotionDiscountAmount;

  @Column(name = "coupon_discount_amount", nullable = false)
  @Comment("쿠폰 할인금액")
  private Integer couponDiscountAmount;

  @Column(name = "final_price", nullable = false)
  @Comment("최종 주문금액")
  private Integer finalPrice;

  private OrderPrice(
      Integer totalProductPrice,
      Integer PromotionDiscountAmount,
      Integer couponDiscountAmount,
      Integer totalDiscountAmount,
      Integer finalPrice) {
    validate(totalProductPrice, totalDiscountAmount);
    this.totalProductPrice = totalProductPrice;
    this.promotionDiscountAmount = PromotionDiscountAmount;
    this.couponDiscountAmount = couponDiscountAmount;
    this.totalDiscountAmount = totalDiscountAmount;
    this.finalPrice = finalPrice;
  }

  private void validate(Integer totalProductPrice, Integer totalDiscountAmount) {
    if (totalDiscountAmount > totalProductPrice) {
      throw new IllegalArgumentException("총 할인 금액이 총 상품 금액을 초과할 수 없습니다.");
    }
  }

  public static OrderPrice of(
      Integer totalProductPrice, Integer promotionDiscountAmount, Integer couponDiscountAmount) {
    Integer totalDiscountAmount = promotionDiscountAmount + couponDiscountAmount;
    Integer finalPrice = totalProductPrice - totalDiscountAmount;
    return new OrderPrice(
        totalProductPrice,
        promotionDiscountAmount,
        couponDiscountAmount,
        totalDiscountAmount,
        finalPrice);
  }

  public OrderPrice useCoupon(Integer couponDiscountAmount) {
    return OrderPrice.of(totalProductPrice, promotionDiscountAmount, couponDiscountAmount);
  }
}
