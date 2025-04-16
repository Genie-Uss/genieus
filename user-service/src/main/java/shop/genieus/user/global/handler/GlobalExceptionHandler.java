package shop.genieus.user.global.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.genieus.common.auth.exception.UnauthorizedException;
import com.genieus.common.response.ApiResponse;
import feign.FeignException.FeignClientException;
import jakarta.servlet.http.HttpServletRequest;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.HandlerMethodValidationException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import shop.genieus.user.global.exception.UserException;

@Slf4j
@RestControllerAdvice
@RequiredArgsConstructor
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {
  private static final int SERVER_ERROR_CODE = 1999;
  private static final int VALIDATION_ERROR_CODE = 1998;
  private static final int REQUEST_PARSING_ERROR_CODE = 1997;
  private static final int UNAUTHORIZED_ERROR_CODE = 7999;
  private static final String SERVER_ERROR_MESSAGE = "서버 내부 오류가 발생했습니다";
  private static final String VALIDATION_ERROR_MESSAGE = "입력값이 유효하지 않습니다";
  private static final String REQUEST_PARSING_ERROR_MESSAGE = "요청 본문을 읽을 수 없습니다";

  private final ObjectMapper objectMapper;

  @ExceptionHandler(UserException.class)
  public ResponseEntity<ApiResponse<Void>> handleUserException(
      UserException exc, HttpServletRequest request) {
    ApiResponse<Void> response = ApiResponse.fail(exc.getCode(), exc.getMessage());
    logError(exc);

    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
  }

  @ExceptionHandler(UnauthorizedException.class)
  public ResponseEntity<ApiResponse<Void>> handleUserException(
      UnauthorizedException exception, HttpServletRequest request) {
    ApiResponse<Void> response = ApiResponse.fail(UNAUTHORIZED_ERROR_CODE, exception.getMessage());
    log.error(
        "Unauthorized Exception - Type: {}, Message: {}, Code: {}",
        exception.getClass().getSimpleName(),
        exception.getMessage(),
        UNAUTHORIZED_ERROR_CODE);

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
          .body(ApiResponse.fail(e.status(), "서비스 처리 중 오류가 발생했습니다."));
    }
  }

  @ExceptionHandler(RuntimeException.class)
  public ResponseEntity<ApiResponse<Void>> handleRuntimeException(
      RuntimeException exc, HttpServletRequest request) {
    ApiResponse<Void> response = ApiResponse.fail(SERVER_ERROR_CODE, SERVER_ERROR_MESSAGE);
    logError(exc, SERVER_ERROR_CODE);

    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
  }

  @Override
  protected ResponseEntity<Object> handleMethodArgumentNotValid(
      MethodArgumentNotValidException exc,
      HttpHeaders headers,
      HttpStatusCode status,
      WebRequest request) {
    Map<String, String> details = extractFieldErrors(exc);
    ApiResponse<Object> response =
        ApiResponse.fail(VALIDATION_ERROR_CODE, VALIDATION_ERROR_MESSAGE);
    logError(VALIDATION_ERROR_CODE, details);

    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
  }

  @Override
  protected ResponseEntity<Object> handleHttpMessageNotReadable(
      HttpMessageNotReadableException exc,
      HttpHeaders headers,
      HttpStatusCode status,
      WebRequest request) {
    ApiResponse<Object> response =
        ApiResponse.fail(REQUEST_PARSING_ERROR_CODE, REQUEST_PARSING_ERROR_MESSAGE);
    logError(exc, REQUEST_PARSING_ERROR_CODE);

    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
  }

  @Override
  protected ResponseEntity<Object> handleHandlerMethodValidationException(
      HandlerMethodValidationException exc,
      HttpHeaders headers,
      HttpStatusCode status,
      WebRequest request) {
    Map<String, String> details = createMethodValidationErrors(exc);
    ApiResponse<Object> response =
        ApiResponse.fail(VALIDATION_ERROR_CODE, VALIDATION_ERROR_MESSAGE);
    logError(VALIDATION_ERROR_CODE, details);

    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
  }

  private void logError(UserException exception) {
    log.error(
        "User Exception - Type: {}, Message: {}, Code: {}",
        exception.getClass().getSimpleName(),
        exception.getMessage(),
        exception.getCode());
  }

  private void logError(Exception exception, int code) {
    log.error(
        "Exception - Type: {}, Message: {}, Code: {}",
        exception.getClass().getSimpleName(),
        exception.getMessage(),
        code);
  }

  private void logError(int code, Map<String, String> details) {
    log.error("Validation Exception - Code: {}, Details: {}", code, details);
  }

  private Map<String, String> extractFieldErrors(MethodArgumentNotValidException exc) {
    return exc.getBindingResult().getFieldErrors().stream()
        .collect(
            Collectors.toMap(
                FieldError::getField,
                FieldError::getDefaultMessage,
                (existing, replacement) -> existing));
  }

  private Map<String, String> createMethodValidationErrors(HandlerMethodValidationException exc) {
    return exc.getParameterValidationResults().stream()
        .collect(
            Collectors.toMap(
                result -> result.getResolvableErrors().get(0).getCodes()[1],
                result -> result.getResolvableErrors().get(0).getDefaultMessage(),
                (existing, replacement) -> existing));
  }
}
