package shop.genieus.product.global.handler;

import static org.springframework.http.HttpStatus.BAD_REQUEST;

import com.genieus.common.response.ApiResponse;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import shop.genieus.product.global.exception.ProductException;

@RestControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(ProductException.class)
  public ResponseEntity<ApiResponse<String>> handleProductException(final ProductException e) {
    return ResponseEntity.status(BAD_REQUEST)
        .body(ApiResponse.fail(e.getErrorCode().getCode(), e.getErrorCode().getMessage()));
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<ApiResponse<Object>> handleMethodArgumentNotValidException(
      MethodArgumentNotValidException e) {
    StringBuilder sb = new StringBuilder();
    e.getBindingResult().getFieldErrors()
        .stream().map(DefaultMessageSourceResolvable::getDefaultMessage)
        .forEach(message -> sb.append(message).append("\n"));

    if (sb.length() > 0 && sb.charAt(sb.length() - 1) == '\n') {
      sb.deleteCharAt(sb.length() - 1);  // 마지막 문자가 개행 문자라면 삭제
    }

    String errorMessages = sb.toString();
    return ResponseEntity.status(BAD_REQUEST).body(ApiResponse.fail(3000, errorMessages));
  }

  @ExceptionHandler(RuntimeException.class)
  public ResponseEntity<ApiResponse<String>> handleRuntimeException(RuntimeException e) {
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
        .body(ApiResponse.fail(2000, e.getMessage()));
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<ApiResponse<String>> handleException(Exception e) {
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
        .body(ApiResponse.fail(2000, e.getMessage()));
  }

  @ExceptionHandler(IllegalArgumentException.class)
  public ResponseEntity<ApiResponse<String>> handleIllegalArgumentException(IllegalArgumentException e) {
    return ResponseEntity.status(BAD_REQUEST)
        .body(ApiResponse.fail(2000, e.getMessage()));
  }
}

