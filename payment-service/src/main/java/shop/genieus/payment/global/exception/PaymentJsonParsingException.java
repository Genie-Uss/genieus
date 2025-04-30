package shop.genieus.payment.global.exception;

import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.Getter;

@Getter
public class PaymentJsonParsingException extends RuntimeException {
  private final JsonProcessingException cause;

  public PaymentJsonParsingException(JsonProcessingException cause) {
    super(cause.getMessage(), cause);
    this.cause = cause;
  }
}