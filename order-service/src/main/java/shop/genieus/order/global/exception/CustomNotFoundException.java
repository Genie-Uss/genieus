package shop.genieus.order.global.exception;

import lombok.Getter;

@Getter
public class CustomNotFoundException extends RuntimeException {
  private final Integer code;

  public CustomNotFoundException(Integer code, String message) {
    super(message);
    this.code = code;
  }

  public static class OrderNotFoundException extends CustomNotFoundException {
    public OrderNotFoundException() {
      super(ErrorCode.ORDER_NOT_FOUND.getCode(), ErrorCode.ORDER_NOT_FOUND.getMessage());
    }
  }

  public static class PromotionNotFoundException extends CustomNotFoundException {
    public PromotionNotFoundException() {
      super(ErrorCode.PROMOTION_NOT_FOUND.getCode(), ErrorCode.PROMOTION_NOT_FOUND.getMessage());
    }
  }

  public static class ProductNotFoundException extends CustomNotFoundException {
    public ProductNotFoundException() {
      super(ErrorCode.PRODUCT_NOT_FOUND.getCode(), ErrorCode.PRODUCT_NOT_FOUND.getMessage());
    }
  }

  public static class CouponNotFoundException extends CustomNotFoundException {
    public CouponNotFoundException() {
      super(ErrorCode.COUPON_NOT_FOUND.getCode(), ErrorCode.COUPON_NOT_FOUND.getMessage());
    }
  }
}
