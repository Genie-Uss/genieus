package shop.genieus.order.global.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.genieus.common.response.ApiResponse;
import feign.FeignException;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import shop.genieus.order.global.exception.CustomBadRequestException;
import shop.genieus.order.global.exception.CustomForbiddenException;
import shop.genieus.order.global.exception.CustomNotFoundException;
import shop.genieus.order.global.exception.CustomServiceUnavailableException;

@Slf4j
@RestControllerAdvice
@RequiredArgsConstructor
public class GlobalExceptionHandler {

  private final ObjectMapper objectMapper;

  @ExceptionHandler(CustomBadRequestException.class)
  protected ResponseEntity<ApiResponse<Void>> handleBadRequestException(
      CustomBadRequestException e) {
    log.error("{} 예외 발생: {}", e.getClass().getSimpleName(), e.getMessage(), e);
    final ApiResponse<Void> response = ApiResponse.fail(e.getCode(), e.getMessage());
    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
  }

  @ExceptionHandler(CustomNotFoundException.class)
  protected ResponseEntity<ApiResponse<Void>> handleNotFoundException(CustomNotFoundException e) {
    log.error("{} 예외 발생: {}", e.getClass().getSimpleName(), e.getMessage(), e);
    final ApiResponse<Void> response = ApiResponse.fail(e.getCode(), e.getMessage());
    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
  }

  @ExceptionHandler(CustomForbiddenException.class)
  protected ResponseEntity<ApiResponse<Void>> handleForbiddenException(CustomForbiddenException e) {
    log.error("{} 예외 발생: {}", e.getClass().getSimpleName(), e.getMessage(), e);
    final ApiResponse<Void> response = ApiResponse.fail(e.getCode(), e.getMessage());
    return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
  }

  @ExceptionHandler(CustomServiceUnavailableException.class)
  protected ResponseEntity<ApiResponse<Void>> handleServiceUnavailableException(
      CustomServiceUnavailableException e) {
    log.error("{} 예외 발생: {}", e.getClass().getSimpleName(), e.getMessage(), e);
    final ApiResponse<Void> response = ApiResponse.fail(e.getCode(), e.getMessage());
    return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(response);
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  protected ResponseEntity<ApiResponse<Void>> handleMethodArgumentNotValidException(
      MethodArgumentNotValidException e) {
    List<ObjectError> errors = e.getBindingResult().getAllErrors();
    String message =
        errors.stream().map(ObjectError::getDefaultMessage).collect(Collectors.joining(","));
    log.error("{} 예외 발생: {}", e.getClass().getSimpleName(), message, e);
    final ApiResponse<Void> response = ApiResponse.fail(e.getStatusCode().value(), message);
    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
  }

  @ExceptionHandler(IllegalArgumentException.class)
  protected ResponseEntity<ApiResponse<Void>> handleIllegalArgumentException(
      IllegalArgumentException e) {
    log.error("{} 예외 발생: {}", e.getClass().getSimpleName(), e.getMessage(), e);
    final ApiResponse<Void> response =
        ApiResponse.fail(HttpStatus.BAD_REQUEST.value(), e.getMessage());
    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
  }

  @ExceptionHandler(FeignException.class)
  protected ResponseEntity<ApiResponse<Void>> handleFeignException(FeignException e) {
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
}
