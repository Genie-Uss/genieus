package shop.genieus.order.domain.model.vo;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public enum OrderStatus {
  ORDER_PENDING("주문대기", true, true),
  PAYMENT_PENDING("결제대기", true, true),
  PAYMENT_COMPLETED("결제완료", true, false),
  DELIVERY_STARTED("배송시작", false, false),
  ORDER_COMPLETED("주문완료", false, false),
  ORDER_CANCELLED("주문취소", false, false),
  ;

  private final String description;
  private final boolean userCancelable;
  private final boolean systemCancelable;

  public boolean canCancelByUser() {
    return userCancelable;
  }

  public boolean canCancelBySystem() {
    return systemCancelable;
  }
}
