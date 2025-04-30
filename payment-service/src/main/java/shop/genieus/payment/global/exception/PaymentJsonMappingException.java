package shop.genieus.payment.global.exception;

import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import lombok.Getter;

@Getter
public class PaymentJsonMappingException extends RuntimeException {
  private final InvalidFormatException cause;

  public PaymentJsonMappingException(InvalidFormatException cause) {
    super(cause.getMessage(), cause);
    this.cause = cause;
  }
}