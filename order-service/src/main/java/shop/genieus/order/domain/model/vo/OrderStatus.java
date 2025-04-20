package shop.genieus.order.domain.model.vo;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public enum OrderStatus {
  ORDER_PENDING("주문대기", true),
  PAYMENT_PENDING("결제대기", true),
  PAYMENT_COMPLETED("결제완료", true),
  ORDER_COMPLETED("주문완료", false),
  ORDER_CANCELLED("주문취소", false),
  ORDER_EXPIRED("주문만료", false),
  ;

  private final String description;
  private final boolean canCancel;

  public boolean canCancel() {
    return canCancel;
  }
}
