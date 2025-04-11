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
@Comment("결테 테이블")
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
    @Column(name = "order_id", nullable = false)
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

    public Payment markAsPaid() {
        if (!isPendingOrFailed()) {
            throw new IllegalArgumentException("결제 상태가 [" + paymentStatus + "] 인 상태에서는 결제 완료할 수 없습니다.");
        }

        this.paymentStatus = PaymentStatus.SUCCESS;
        this.paymentPaidAt = LocalDateTime.now();
        return this;
    }

    private boolean isPendingOrFailed() {
        return paymentStatus == PaymentStatus.PENDING
                || paymentStatus == PaymentStatus.FAILED;
    }
}
