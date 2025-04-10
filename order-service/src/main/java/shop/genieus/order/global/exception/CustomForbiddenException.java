package shop.genieus.order.global.exception;

import lombok.Getter;

@Getter
public class CustomForbiddenException extends RuntimeException {
  private final Integer code;

  public CustomForbiddenException(Integer code, String message) {
    super(message);
    this.code = code;
  }
}
