package shop.genieus.payment.domain.model.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import lombok.*;
import org.hibernate.annotations.Comment;
import shop.genieus.payment.domain.assembler.CreatePaymentAssembler;
import shop.genieus.payment.domain.model.vo.Money;
import shop.genieus.payment.domain.model.vo.PaymentMethod;
import shop.genieus.payment.domain.model.vo.PaymentStatus;
import shop.genieus.payment.global.exception.PaymentErrorCode;
import shop.genieus.payment.global.exception.PaymentException;

@Entity
@Table(name = "m_payment")
@Comment("결제 테이블")
@Getter @Builder(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Payment extends BaseEntity {

  @Id
  @Comment("결제 식별자")
  @Column(name = "payment_id")
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long paymentId;

  @Comment("PG 식별자")
  @Column(name = "pg_id")
  private Long pgId;

  @Comment("유저 식별자")
  @Column(name = "user_id", nullable = false)
  private Long userId;

  @Comment("주문 식별자")
  @Column(name = "order_id", unique = true, nullable = false)
  private Long orderId;

  @Embedded
  @Comment("결제 금액")
  @Column(name = "payment_price", nullable = false)
  private Money paymentPrice;

  @Comment("결제 수단")
  @Column(name = "payment_method")
  @Enumerated(EnumType.STRING)
  private PaymentMethod paymentMethod;

  @Comment("결제 상태")
  @Column(name = "payment_status", nullable = false)
  @Enumerated(EnumType.STRING)
  private PaymentStatus paymentStatus;

  @Comment("결제 완료 일시")
  @Column(name = "payment_paid_at")
  private LocalDateTime paymentPaidAt;

  public static Payment create(CreatePaymentAssembler assembler) {
    return Payment.builder()
        .userId(assembler.userId())
        .orderId(assembler.orderId())
        .paymentPrice(new Money(assembler.paymentPrice()))
        .paymentStatus(PaymentStatus.PENDING)
        .build();
  }

  public Payment cancel() {
    checkPaymentStatusForCancel();
    checkIsRefundable();

    this.paymentStatus = PaymentStatus.REFUNDED;

    return this;
  }

  public void setPaymentMethod(String method) {
    paymentMethod = parsePaymentMethod(method);
  }

  public void setPaymentSuccessForTest() {
    paymentMethod = PaymentMethod.TEST;
    registerPaymentSuccess();
  }

  private PaymentMethod parsePaymentMethod(String method) {
    try {
      return PaymentMethod.valueOf(method.toUpperCase());
    } catch (Exception e) {
      throw new PaymentException(PaymentErrorCode.INVALID_PAYMENT_METHOD);
    }
  }

  public void registerPaymentSuccess() {
    checkPaymentStatusForRegister();

    paymentStatus = PaymentStatus.SUCCESS;
    paymentPaidAt = LocalDateTime.now();
  }

  private void checkPaymentStatusForRegister() {
    switch (this.paymentStatus) {
      case SUCCESS -> throw new PaymentException(PaymentErrorCode.INVALID_PAYMENT_REGISTER_PAID);
      case REFUNDED -> throw new PaymentException(PaymentErrorCode.INVALID_PAYMENT_REGISTER_REFUNDED);
    }
  }

  private void checkIsRefundable() {
    if (this.paymentPaidAt.isBefore(LocalDateTime.now().minusDays(7))) {
      throw new IllegalArgumentException("결제 완료 후 7일이 지난 결제는 취소할 수 없습니다.");
    }
  }

  private void checkPaymentStatusForCancel() {
    switch (this.paymentStatus) {
      case PENDING -> throw new PaymentException(PaymentErrorCode.INVALID_PAYMENT_CANCEL_PENDING);
      case FAILED -> throw new PaymentException(PaymentErrorCode.INVALID_PAYMENT_CANCEL_FAILED);
      case REFUNDED -> throw new PaymentException(PaymentErrorCode.INVALID_PAYMENT_CANCEL_REFUNDED);
    }
  }
}
