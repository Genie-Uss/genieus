package shop.genieus.order.domain.model.vo;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;

@Getter
@Embeddable
@NoArgsConstructor
public class OrderTimeStamp {

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
