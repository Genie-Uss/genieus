package shop.genieus.payment.global.exception;

import com.genieus.common.response.ApiResponse;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(PaymentException.class)
  public ResponseEntity<ApiResponse<HttpStatusCode>> handlePaymentException(PaymentException e) {
    return ResponseEntity.status(e.getHttpStatus())
        .body(ApiResponse.fail(e.getCode(), e.getMessage()));
  }
}
