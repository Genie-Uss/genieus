package shop.genieus.order.domain.model.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Comment;
import shop.genieus.order.domain.model.assembler.OrderProductAssembler;
import shop.genieus.order.domain.model.vo.Product;
import shop.genieus.order.domain.model.vo.Promotion;
import shop.genieus.order.domain.model.vo.Quantity;

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

  @Embedded private Product product;

  @Embedded private Promotion promotion;

  @Embedded private Quantity quantity;

  public static OrderProduct create(OrderProductAssembler assembler) {
    return OrderProduct.builder()
        .product(Product.of(assembler.getProductId(), assembler.getProductPrice()))
        .promotion(Promotion.of(assembler.getPromotionId(), assembler.getPromotionDiscountRate()))
        .quantity(Quantity.of(assembler.getQuantity()))
        .build();
  }

  protected void setOrder(Order order) {
    this.order = order;
  }
}
