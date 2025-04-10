package shop.genieus.order.global.exception;

import lombok.Getter;

@Getter
public class CustomBadRequestException extends RuntimeException {

  private final Integer code;

  public CustomBadRequestException(Integer code, String message) {
    super(message);
    this.code = code;
  }
}
