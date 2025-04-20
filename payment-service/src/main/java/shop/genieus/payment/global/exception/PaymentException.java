package shop.genieus.payment.global.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class PaymentException extends RuntimeException {
  private final int code;
  private final HttpStatus httpStatus;

  public PaymentException(PaymentErrorCode errorCode) {
    super(errorCode.getMessage());
    this.code = errorCode.getCode();
    this.httpStatus = errorCode.getHttpStatus();
  }

  public PaymentException(PaymentErrorCode errorCode, Throwable cause) {
    super(errorCode.getMessage(), cause);
    this.code = errorCode.getCode();
    this.httpStatus = errorCode.getHttpStatus();
  }
}
