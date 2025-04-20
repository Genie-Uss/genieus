package shop.genieus.payment.global.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum PaymentErrorCode {
  PAYMENT_FEIGN_FAILED(6001, "결제 요청 통신에 실패하였습니다.", HttpStatus.BAD_GATEWAY),
  PAYMENT_DUPLICATED_ERROR(6002, "결제 객체를 중복 생성할 수 없습니다.", HttpStatus.CONFLICT),
  PAYMENT_REQUEST_FAILED(6003, "결제 요청 처리에 실패하였습니다.", HttpStatus.INTERNAL_SERVER_ERROR);

  private final int code;
  private final String message;
  private final HttpStatus httpStatus;

  PaymentErrorCode(int code, String message, HttpStatus httpStatus) {
    this.code = code;
    this.message = message;
    this.httpStatus = httpStatus;
  }
}
