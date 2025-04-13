package shop.genieus.payment.domain.model.vo;

import lombok.Getter;

@Getter
public enum PaymentStatus {
    PENDING("결제 대기 상태"),
    SUCCESS("결제 성공"),
    FAILED("결제 실패"), // 결제 요청 후 정한 시간 동안 결제하지 않은 경우
    REFUNDED("환불 완료");

    private final String description;

    PaymentStatus(String description) {
        this.description = description;
    }
}
