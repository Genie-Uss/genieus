package shop.genieus.payment.domain.model.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Comment;
import shop.genieus.payment.domain.assembler.CreatePaymentAssembler;
import shop.genieus.payment.domain.model.vo.Money;
import shop.genieus.payment.domain.model.vo.PaymentMethod;
import shop.genieus.payment.domain.model.vo.PaymentStatus;

import java.time.LocalDateTime;

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
        checkPaymentStatusForRegister();
        paymentStatus = PaymentStatus.SUCCESS;
    }

    private PaymentMethod parsePaymentMethod(String method) {
        try {
            return PaymentMethod.valueOf(method.toUpperCase());
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid payment method: " + paymentMethod);
        }
    }

    public void registerPaymentSuccess() {
        checkPaymentStatusForRegister();

        paymentStatus = PaymentStatus.SUCCESS;
        paymentPaidAt = LocalDateTime.now();
    }

    private void checkPaymentStatusForRegister() {
        switch (this.paymentStatus) {
            case SUCCESS -> throw new IllegalArgumentException("완료된 결제를 시도할 수 없습니다.");
            case REFUNDED -> throw new IllegalArgumentException("환불된 결제를 시도할 수 없습니다.");
        }
    }


    private void checkIsRefundable() {
        // TODO 일단 결제 완료 후 7일 이내 환불 정책으로 했는데, 배송 시작 기간 받아야 함?
        if (this.paymentPaidAt.isBefore(LocalDateTime.now().minusDays(7))) {
            throw new IllegalArgumentException("결제 완료 후 7일이 지난 결제는 취소할 수 없습니다.");
        }
    }

    private void checkPaymentStatusForCancel() {
        switch (this.paymentStatus) {
            case PENDING -> throw new IllegalArgumentException("결제 진행 중인 경우 환불할 수 없습니다.");
            case FAILED -> throw new IllegalArgumentException("실패한 결제를 환불할 수 없습니다.");
            case REFUNDED -> throw new IllegalArgumentException("이미 환불한 결제입니다.");
       }
    }
}
