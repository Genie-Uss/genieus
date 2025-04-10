package shop.genieus.order.domain.model.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Comment;
import shop.genieus.order.domain.model.assembler.OrderProductAssembler;

@Entity
@Getter
@Comment("주문상품 테이블")
@Table(name = "m_order_product")
@Builder(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class OrderProduct {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "order_product_id")
  @Comment("주문상품 식별자")
  private Long orderProductId;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "order_id", nullable = false)
  @Comment("주문 식별자")
  private Order order;

  @Column(name = "product_id", nullable = false)
  @Comment("상품 식별자")
  private Long productId;

  @Column(name = "promotion_id", nullable = false)
  @Comment("프로모션 식별자")
  private Long promotionId;

  @Column(name = "promotion_discount_rate", nullable = false)
  @Comment("프로모션 할인금액")
  private Integer promotionDiscountRate;

  @Column(name = "product_price", nullable = false)
  @Comment("상품 정상가")
  private Integer productPrice;

  @Column(name = "quantity", nullable = false)
  @Comment("수량")
  private Integer quantity;

  public static OrderProduct create(OrderProductAssembler assembler) {
    return OrderProduct.builder()
        .productId(assembler.getProductId())
        .promotionId(assembler.getPromotionId())
        .promotionDiscountRate(assembler.getPromotionDiscountRate())
        .productPrice(assembler.getProductPrice())
        .quantity(assembler.getQuantity())
        .build();
  }

  protected void setOrder(Order order) {
    this.order = order;
  }
}
