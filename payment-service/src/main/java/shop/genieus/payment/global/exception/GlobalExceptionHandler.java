package shop.genieus.payment.global.exception;

import com.genieus.common.response.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(PaymentException.class)
  public ResponseEntity<ApiResponse<HttpStatusCode>> handlePaymentException(PaymentException e) {
    log.warn("[결제 서비스] 예외: {}", e.getMessage());
    return ResponseEntity.status(e.getHttpStatus())
        .body(ApiResponse.fail(e.getCode(), e.getMessage()));
  }
}
