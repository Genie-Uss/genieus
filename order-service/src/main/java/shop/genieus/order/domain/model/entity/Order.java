package shop.genieus.order.domain.model.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.*;
import org.hibernate.annotations.Comment;
import shop.genieus.order.domain.model.assembler.CreateOrderAssembler;
import shop.genieus.order.domain.model.vo.OrderPrice;
import shop.genieus.order.domain.model.vo.OrderStatus;
import shop.genieus.order.domain.model.vo.Receiver;

@Entity
@Getter
@Comment("주문 테이블")
@Table(name = "m_order")
@Builder(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Order extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "order_id")
  @Comment("주문 식별자")
  private Long orderId;

  @Column(name = "user_id")
  @Comment("주문한 유저 식별자")
  private Long userId;

  @Embedded private Receiver receiver;

  @Column(name = "coupon_id")
  @Comment("쿠폰 식별자")
  private Long couponId;

  @Enumerated(EnumType.STRING)
  @Column(name = "status", nullable = false)
  @Comment("주문상태")
  private OrderStatus status;

  @Embedded private OrderPrice orderPrice;

  @Column(name = "ordered_at", nullable = false)
  @Comment("주문일시")
  private LocalDateTime orderedAt;

  @Column(name = "payment_requested_at")
  @Comment("결제요청일시")
  private LocalDateTime paymentRequestedAt;

  @Column(name = "canceled_at")
  @Comment("주문취소일시")
  private LocalDateTime canceledAt;

  @Column(name = "paid_at")
  @Comment("결제완료일시")
  private LocalDateTime paidAt;

  @Column(name = "completed_at")
  @Comment("주문완료일시")
  private LocalDateTime completedAt;

  @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
  @Comment("주문상품 리스트")
  private List<OrderProduct> orderProducts;

  public static Order create(CreateOrderAssembler assembler) {
    OrderPrice orderPrice =
        OrderPrice.of(assembler.getTotalProductPrice(), assembler.getPromotionDiscountAmount(), 0);

    Order order =
        Order.builder()
            .userId(assembler.getUserId())
            .status(OrderStatus.ORDER_PENDING)
            .orderedAt(assembler.getOrderedAt())
            .orderProducts(new ArrayList<>())
            .orderPrice(orderPrice)
            .build();

    assembler
        .getOrderProducts()
        .forEach(
            opAssembler -> {
              OrderProduct orderProduct = OrderProduct.create(opAssembler);
              order.addOrderProduct(orderProduct);
            });
    return order;
  }

  public void addOrderProduct(OrderProduct orderProduct) {
    this.orderProducts.add(orderProduct);
    orderProduct.setOrder(this);
  }

  public void useCoupon(Integer couponDiscountAmount) {
    this.orderPrice = this.orderPrice.useCoupon(couponDiscountAmount);
  }

  public void requestPayment(LocalDateTime paymentRequestedAt) {
    this.paymentRequestedAt = paymentRequestedAt;
    this.status = OrderStatus.PAYMENT_PENDING;
  }

  public void cancel(LocalDateTime canceledAt) {
    this.canceledAt = canceledAt;
    this.status = OrderStatus.ORDER_CANCELLED;
  }

  public void startDelivery(LocalDateTime deliveryStartedAt) {
    this.status = OrderStatus.DELIVERY_STARTED;
  }

  public void completePayment(LocalDateTime paidAt) {
    this.paidAt = paidAt;
    this.status = OrderStatus.PAYMENT_COMPLETED;
  }

  public void completeOrder(LocalDateTime completedAt) {
    this.completedAt = completedAt;
    this.status = OrderStatus.ORDER_COMPLETED;
  }
}
