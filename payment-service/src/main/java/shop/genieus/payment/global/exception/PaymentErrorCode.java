package shop.genieus.payment.global.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum PaymentErrorCode {
  PAYMENT_FEIGN_FAILED(6001, "결제 요청 통신에 실패하였습니다.", HttpStatus.BAD_GATEWAY),
  PAYMENT_DUPLICATED_ERROR(6002, "결제 객체를 중복 생성할 수 없습니다.", HttpStatus.CONFLICT),
  PAYMENT_REQUEST_FAILED(6003, "결제 요청 처리에 실패하였습니다.", HttpStatus.INTERNAL_SERVER_ERROR),
  INVALID_PAYMENT_METHOD(6004, "지원하지 않는 결제 수단입니다.", HttpStatus.BAD_REQUEST),
  INVALID_PAYMENT_REGISTER_PAID(6005, "이미 결제 완료된 주문을 결제할 수 없습니다.", HttpStatus.BAD_REQUEST),
  INVALID_PAYMENT_REGISTER_REFUNDED(6006, "이미 환불된 주문을 결제할 수 없습니다.", HttpStatus.BAD_REQUEST),
  INVALID_PAYMENT_CANCEL_PENDING(6007, "결제 대기 중인 주문을 취소할 수 없습니다.", HttpStatus.BAD_REQUEST),
  INVALID_PAYMENT_CANCEL_FAILED(6008, "결제 실패한 주문을 취소할 수 없습니다.", HttpStatus.BAD_REQUEST),
  INVALID_PAYMENT_CANCEL_REFUNDED(6009, "이미 환불된 주문을 취소할 수 없습니다.", HttpStatus.BAD_REQUEST),

  COMPLETED_PAYMENT_RESULT_EMPTY(6100, "이벤트 파싱 과정에서 문제가 발생하였습니다.", HttpStatus.INTERNAL_SERVER_ERROR),

  JSON_PARSING_ERROR(6901, "JSON 파싱 문법에 문제가 있습니다.", HttpStatus.INTERNAL_SERVER_ERROR),
  JSON_MAPPING_FAILED(6902, "JSON 매핑에 실패하였습니다.", HttpStatus.INTERNAL_SERVER_ERROR),
  JSON_PROCESSING_FAILED(6903, "JSON 파싱에 실패하였습니다.", HttpStatus.INTERNAL_SERVER_ERROR);

  private final int code;
  private final String message;
  private final HttpStatus httpStatus;

  PaymentErrorCode(int code, String message, HttpStatus httpStatus) {
    this.code = code;
    this.message = message;
    this.httpStatus = httpStatus;
  }
}
