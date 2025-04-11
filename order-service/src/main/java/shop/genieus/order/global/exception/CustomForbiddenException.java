package shop.genieus.order.global.exception;

import lombok.Getter;

@Getter
public class CustomForbiddenException extends RuntimeException {
  private final Integer code;

  public CustomForbiddenException(Integer code, String message) {
    super(message);
    this.code = code;
  }

  public static class AccessOrderForbiddenException extends CustomForbiddenException {
    public AccessOrderForbiddenException() {
      super(
          ErrorCode.ACCESS_ORDER_FORBIDDEN.getCode(),
          ErrorCode.ACCESS_ORDER_FORBIDDEN.getMessage());
    }
  }
}
