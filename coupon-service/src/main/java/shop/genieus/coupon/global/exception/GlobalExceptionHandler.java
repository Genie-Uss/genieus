package shop.genieus.coupon.global.exception;

import com.genieus.common.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
@RequiredArgsConstructor
public class GlobalExceptionHandler {

  @ExceptionHandler(IllegalArgumentException.class)
  protected ResponseEntity<ApiResponse<Void>> handleIllegalArgumentException(
      IllegalArgumentException e) {
    log.warn("{} 예외 발생: {}", e.getClass().getSimpleName(), e.getMessage(), e);
    final ApiResponse<Void> response =
        ApiResponse.fail(HttpStatus.BAD_REQUEST.value(), e.getMessage());
    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
  }
}
