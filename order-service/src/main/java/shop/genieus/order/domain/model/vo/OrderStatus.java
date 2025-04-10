package shop.genieus.order.domain.model.vo;

import lombok.Getter;

@Getter
public enum OrderStatus {
  ORDER_PENDING("주문대기"),
  PAYMENT_PENDING("결제대기"),
  PAYMENT_COMPLETED("결제완료"),
  DELIVERY_STARTED("배송시작"),
  ORDER_COMPLETED("주문완료"),
  ORDER_CANCELLED("주문취소");

  private final String description;

  OrderStatus(String description) {
    this.description = description;
  }
}
