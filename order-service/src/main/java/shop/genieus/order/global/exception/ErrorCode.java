package shop.genieus.order.global.exception;

import lombok.Getter;

@Getter
public enum ErrorCode {
  ORDER_NOT_FOUND(4001, "해당 주문이 존재하지 않습니다."),
  CREATE_ORDER_FORBIDDEN(4002, "주문을 생성할 권한이 없습니다."),
  UPDATE_ORDER_FORBIDDEN(4003, "주문을 수정할 권한이 없습니다."),
  DELETE_ORDER_FORBIDDEN(4004, "주문을 삭제할 권한이 없습니다."),
  ACCESS_ORDER_FORBIDDEN(4005, "해당 주문을 접근할 권한이 없습니다."),
  CANCEL_ORDER_FORBIDDEN(4006, "주문을 취소할 권한이 없습니다."),
  CANCEL_ORDER_BAD_REQUEST(4007, "주문을 취소할 수 없습니다."),

  PRODUCT_NOT_FOUND(4101, "상품 서비스에서 유효한 데이터가 존재하지 않습니다."),
  COUPON_NOT_FOUND(4102, "쿠폰 서비스에서 유효한 데이터가 존재하지 않습니다."),
  PROMOTION_NOT_FOUND(4103, "프로모션 서비스에서 유효한 데이터가 존재하지 않습니다."),

  ORDER_SERVICE_FAILURE(4200, "요청 처리 중 오류가 발생했습니다. 잠시 후 다시 시도해주세요."),
  PROMOTION_SERVICE_FAILURE(4201, "프로모션 서비스 호출에 실패했습니다."),
  PRODUCT_SERVICE_FAILURE(4202, "상품 서비스 호출에 실패했습니다."),
  COUPON_SERVICE_FAILURE(4203, "쿠폰 서비스 호출에 실패했습니다."),
  PAYMENT_SERVICE_FAILURE(4204, "결제 서비스 호출에 실패했습니다."),
  ;
  private final Integer code;
  private final String message;

  ErrorCode(final Integer code, final String message) {
    this.code = code;
    this.message = message;
  }
}
