package shop.genieus.order.global.exception;

import lombok.Getter;

@Getter
public class CustomServiceUnavailableException extends RuntimeException {
  private final Integer code;

  public CustomServiceUnavailableException(Integer code, String message) {
    super(message);
    this.code = code;
  }

  public static class PromotionServiceFailureException extends CustomServiceUnavailableException {
    public PromotionServiceFailureException() {
      super(
          ErrorCode.PROMOTION_SERVICE_FAILURE.getCode(),
          ErrorCode.PROMOTION_SERVICE_FAILURE.getMessage());
    }
  }

  public static class ProductServiceFailureException extends CustomServiceUnavailableException {
    public ProductServiceFailureException() {
      super(
          ErrorCode.PRODUCT_SERVICE_FAILURE.getCode(),
          ErrorCode.PRODUCT_SERVICE_FAILURE.getMessage());
    }
  }

  public static class CouponServiceFailureException extends CustomServiceUnavailableException {
    public CouponServiceFailureException() {
      super(
          ErrorCode.COUPON_SERVICE_FAILURE.getCode(),
          ErrorCode.COUPON_SERVICE_FAILURE.getMessage());
    }
  }
}
