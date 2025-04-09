package shop.genieus.promotion.global.exception;

import lombok.Getter;
import shop.genieus.promotion.global.constants.Code;

@Getter
public class PromotionException extends RuntimeException {

  private final Code errorCode;

  public PromotionException(Code errorCode) {
    super(errorCode.getMessage());
    this.errorCode = errorCode;
  }
}
