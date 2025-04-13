package shop.genieus.product.global.handler;

import com.genieus.common.auth.exception.UnauthorizedException;
import com.genieus.common.response.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import java.util.Map;
import java.util.stream.Collectors;
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
import shop.genieus.product.global.exception.InsufficientStockException;
import shop.genieus.product.global.exception.ProductException;
import shop.genieus.product.global.exception.ProductNotFoundException;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

  private static final int DEFAULT_ERROR_CODE = 2999;
  private static final int VALIDATION_ERROR_CODE = 2998;
  private static final int REQUEST_PARSING_ERROR_CODE = 2997;

  private static final String SERVER_ERROR_MESSAGE = "서버 내부 오류가 발생했습니다";
  private static final String VALIDATION_ERROR_MESSAGE = "입력값이 유효하지 않습니다";
  private static final String REQUEST_PARSING_ERROR_MESSAGE = "요청 본문을 읽을 수 없습니다";

  @ExceptionHandler(ProductNotFoundException.class)
  public ResponseEntity<ApiResponse<Void>> handleProductNotFoundException(
      ProductNotFoundException e, HttpServletRequest request) {
    ApiResponse<Void> response = ApiResponse.fail(e.getCode(), e.getMessage());
    logError(e);
    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
  }

  @ExceptionHandler(InsufficientStockException.class)
  public ResponseEntity<ApiResponse<Void>> handleInsufficientStockException(
      InsufficientStockException e, HttpServletRequest request) {
    ApiResponse<Void> response = ApiResponse.fail(e.getCode(), e.getMessage());
    logError(e);
    return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
  }

  @ExceptionHandler(ProductException.class)
  public ResponseEntity<ApiResponse<Void>> handleProductException(
      ProductException e, HttpServletRequest request) {
    ApiResponse<Void> response = ApiResponse.fail(e.getCode(), e.getMessage());
    logError(e);
    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
  }

  @ExceptionHandler(UnauthorizedException.class)
  public ResponseEntity<ApiResponse<Void>> handleUserException(
      UnauthorizedException exception, HttpServletRequest request) {
    ApiResponse<Void> response = ApiResponse.fail(DEFAULT_ERROR_CODE, exception.getMessage());
    log.error(
        "Unauthorized Exception - Type: {}, Message: {}, Code: {}",
        exception.getClass().getSimpleName(),
        exception.getMessage(),
        DEFAULT_ERROR_CODE);

    return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
  }

  @ExceptionHandler(IllegalArgumentException.class)
  public ResponseEntity<ApiResponse<Void>> handleIllegalArgumentException(
      IllegalArgumentException e, HttpServletRequest request) {
    ApiResponse<Void> response = ApiResponse.fail(DEFAULT_ERROR_CODE, e.getMessage());
    logError(e, DEFAULT_ERROR_CODE);
    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
  }

  @ExceptionHandler(RuntimeException.class)
  public ResponseEntity<ApiResponse<Void>> handleRuntimeException(
      RuntimeException e, HttpServletRequest request) {
    ApiResponse<Void> response = ApiResponse.fail(DEFAULT_ERROR_CODE, SERVER_ERROR_MESSAGE);
    logError(e, DEFAULT_ERROR_CODE);
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
  }

  @Override
  protected ResponseEntity<Object> handleMethodArgumentNotValid(
      MethodArgumentNotValidException e,
      HttpHeaders headers,
      HttpStatusCode status,
      WebRequest request) {
    Map<String, String> details = extractFieldErrors(e);
    ApiResponse<Object> response =
        ApiResponse.fail(VALIDATION_ERROR_CODE, VALIDATION_ERROR_MESSAGE);
    logError(e, VALIDATION_ERROR_CODE, details);

    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
  }

  @Override
  protected ResponseEntity<Object> handleHttpMessageNotReadable(
      HttpMessageNotReadableException e,
      HttpHeaders headers,
      HttpStatusCode status,
      WebRequest request) {
    ApiResponse<Object> response =
        ApiResponse.fail(REQUEST_PARSING_ERROR_CODE, REQUEST_PARSING_ERROR_MESSAGE);
    logError(e, REQUEST_PARSING_ERROR_CODE);

    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
  }

  @Override
  protected ResponseEntity<Object> handleHandlerMethodValidationException(
      HandlerMethodValidationException e,
      HttpHeaders headers,
      HttpStatusCode status,
      WebRequest request) {
    Map<String, String> details = createMethodValidationErrors(e);
    ApiResponse<Object> response =
        ApiResponse.fail(VALIDATION_ERROR_CODE, VALIDATION_ERROR_MESSAGE);
    logError(e, VALIDATION_ERROR_CODE, details);

    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
  }

  private void logError(ProductException exception) {
    log.error(
        "Product Exception - Type: {}, Message: {}, Code: {}",
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

  private void logError(Exception exception, int code, Map<String, String> details) {
    log.error(
        "Validation Exception - Type: {}, Message: {}, Code: {}, Details: {}",
        exception.getClass().getSimpleName(),
        exception.getMessage(),
        code,
        details);
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
