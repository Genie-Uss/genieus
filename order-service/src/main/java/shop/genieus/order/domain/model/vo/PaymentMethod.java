package shop.genieus.order.domain.model.vo;

import lombok.Getter;

@Getter
public enum PaymentMethod {
  CARD("카드"),
  VIRTUAL_ACCOUNT("가상계좌"),
  MOBILE("휴대폰"),
  TOSS_PAY("토스페이"),
  NAVER_PAY("네이버페이"),
  KAKAO_PAY("카카오페이"),
  GENIEUS_PAY("지니어스페이"),
  SAMSUNG_PAY("삼성페이"),
  APPLE_PAY("애플페이");

  private final String displayName;

  PaymentMethod(String displayName) {
    this.displayName = displayName;
  }
}
