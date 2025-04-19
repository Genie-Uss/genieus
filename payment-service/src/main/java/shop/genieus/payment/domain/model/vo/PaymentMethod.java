package shop.genieus.payment.domain.model.vo;

import lombok.Getter;

@Getter
public enum PaymentMethod {
    TEST("테스트용"),
    CARD("카드"),
    VIRTUAL_ACCOUNT("가상계좌"),
    MOBILE("휴대폰"),
    TOSS_PAY("토스페이"),
    NAVER_PAY("네이버페이"),
    KAKAO_PAY("카카오페이");

    private final String displayName;

    PaymentMethod(String displayName) {
        this.displayName = displayName;
    }
}
