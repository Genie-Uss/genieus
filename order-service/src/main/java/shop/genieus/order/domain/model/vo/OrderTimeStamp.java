package shop.genieus.order.domain.model.vo;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;

@Getter
@Embeddable
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class OrderTimeStamp {

  @Column(name = "ordered_at", nullable = false)
  @Comment("주문일시")
  private LocalDateTime orderedAt;

  @Column(name = "order_deadline_at", nullable = false)
  @Comment("주문만료 기한")
  private LocalDateTime orderDeadlineAt;

  @Column(name = "payment_requested_at")
  @Comment("결제요청일시")
  private LocalDateTime paymentRequestedAt;

  @Column(name = "payment_completed_at")
  @Comment("결제완료일시")
  private LocalDateTime paymentCompletedAt;

  @Column(name = "order_completed_at")
  @Comment("주문완료일시")
  private LocalDateTime orderCompletedAt;

  @Column(name = "order_canceled_at")
  @Comment("주문취소일시")
  private LocalDateTime orderCanceledAt;

  @Column(name = "order_expired_at")
  @Comment("주문만료일시")
  private LocalDateTime orderExpiredAt;

  private OrderTimeStamp(LocalDateTime orderedAt, LocalDateTime orderDeadlineAt) {
    this.orderedAt = orderedAt;
    this.orderDeadlineAt = orderDeadlineAt;
  }

  public static OrderTimeStamp of(LocalDateTime orderedAt, LocalDateTime orderDeadlineAt) {
    return new OrderTimeStamp(orderedAt, orderDeadlineAt);
  }

  public void markPaymentRequestedAt(LocalDateTime at) {
    this.paymentRequestedAt = at;
  }

  public void markPaymentCompletedAt(LocalDateTime at) {
    this.paymentCompletedAt = at;
  }

  public void markOrderCompletedAt(LocalDateTime at) {
    this.orderCompletedAt = at;
  }

  public void markOrderCanceledAt(LocalDateTime at) {
    this.orderCanceledAt = at;
  }

  public void markOrderExpiredAt(LocalDateTime at) {
    this.orderExpiredAt = at;
  }
}
