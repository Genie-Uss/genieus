package shop.genieus.product.global.exception;

import lombok.Getter;
import shop.genieus.product.global.constants.Code;

@Getter
public class ProductException extends RuntimeException {
  private final Code errorCode;

  public ProductException(Code errorCode) {
    super(errorCode.getMessage());
    this.errorCode = errorCode;
  }
}
