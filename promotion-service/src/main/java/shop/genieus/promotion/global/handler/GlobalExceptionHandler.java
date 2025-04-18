package shop.genieus.promotion.global.handler;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.genieus.common.auth.exception.UnauthorizedException;
import com.genieus.common.response.ApiResponse;
import feign.FeignException.FeignClientException;
import feign.FeignException.FeignServerException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.support.MethodArgumentTypeMismatchException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import shop.genieus.promotion.global.exception.PromotionException;

@Slf4j
@RestControllerAdvice
@RequiredArgsConstructor
public class GlobalExceptionHandler {

  private final ObjectMapper objectMapper;

  @ExceptionHandler(PromotionException.class)
  public ResponseEntity<ApiResponse<String>> handlePromotionException(final PromotionException e) {
    return ResponseEntity.status(BAD_REQUEST)
        .body(ApiResponse.fail(e.getErrorCode().getCode(), e.getErrorCode().getMessage()));
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<ApiResponse<Object>> handleMethodArgumentNotValidException(
      MethodArgumentNotValidException e) {
    StringBuilder sb = new StringBuilder();
    e.getBindingResult().getFieldErrors().stream()
        .map(DefaultMessageSourceResolvable::getDefaultMessage)
        .forEach(message -> sb.append(message).append("\n"));

    if (sb.length() > 0 && sb.charAt(sb.length() - 1) == '\n') {
      sb.deleteCharAt(sb.length() - 1); // 마지막 문자가 개행 문자라면 삭제
    }

    String errorMessages = sb.toString();
    return ResponseEntity.status(BAD_REQUEST).body(ApiResponse.fail(3999, errorMessages));
  }

  @ExceptionHandler(MethodArgumentTypeMismatchException.class)
  public ResponseEntity<ApiResponse<Object>> handleTypeMismatch(MethodArgumentTypeMismatchException e) {
    return ResponseEntity.badRequest().body(ApiResponse.fail(3999, "잘못된 타입의 요청입니다."));
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<ApiResponse<Void>> handleException(Exception e) {
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
        .body(ApiResponse.fail(3999, e.getMessage()));
  }

  @ExceptionHandler(UnauthorizedException.class)
  public ResponseEntity<ApiResponse<Void>> handleUserException(
      UnauthorizedException exception, HttpServletRequest request) {
    ApiResponse<Void> response = ApiResponse.fail(7999, exception.getMessage());
    log.error(
        "Unauthorized Exception - Type: {}, Message: {}, Code: {}",
        exception.getClass().getSimpleName(),
        exception.getMessage(),
        7999);

    return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
  }

  @ExceptionHandler(FeignClientException.class)
  protected ResponseEntity<ApiResponse<Void>> handleFeignClientException(FeignClientException e) {
    try {
      String content = e.contentUTF8();
      ApiResponse<?> response = objectMapper.readValue(content, ApiResponse.class);
      log.error("{} 예외 발생: {}", e.getClass().getSimpleName(), e.getMessage(), e);
      return ResponseEntity.status(e.status())
          .body(ApiResponse.fail(response.code(), response.message()));
    } catch (Exception ex) {
      log.error("{} 예외 발생: {}", ex.getClass().getSimpleName(), ex.getMessage(), ex);
      return ResponseEntity.status(e.status())
          .body(ApiResponse.fail(e.status(), "Feign 예외 발생 (Body 파싱 실패)"));
    }
  }

  @ExceptionHandler(FeignServerException.class)
  protected ResponseEntity<ApiResponse<Void>> handleFeignClientException(FeignServerException e) {
    log.error("{} 예외 발생: {}", e.getClass().getSimpleName(), e.getMessage(), e);
    return ResponseEntity.status(e.status()).body(ApiResponse.fail(3999, "서버 오류 발생"));
  }

  @ExceptionHandler(RuntimeException.class)
  protected ResponseEntity<ApiResponse<Void>> handleRuntimeException(RuntimeException e) {
    log.error("{} 예외 발생: {}", e.getClass().getSimpleName(), e.getMessage(), e);
    return ResponseEntity.status(INTERNAL_SERVER_ERROR).body(ApiResponse.fail(3999, e.getMessage()));
  }
}
