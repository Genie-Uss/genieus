package shop.genieus.product.global.exception;

import lombok.Getter;

@Getter
public class ProductException extends RuntimeException {
  private static final int DEFAULT_CODE = 2999;
  private final int code;

  protected ProductException(String message, int code) {
    super(message);
    this.code = code;
  }

  protected ProductException(String message) {
    this(message, DEFAULT_CODE);
  }

  protected ProductException(String message, Throwable cause, int code) {
    super(message, cause);
    this.code = code;
  }

  public ProductException(String message, Throwable cause) {
    this(message, cause, DEFAULT_CODE);
  }
}
