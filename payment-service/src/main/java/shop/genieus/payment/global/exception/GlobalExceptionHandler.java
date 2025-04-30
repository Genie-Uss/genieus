package shop.genieus.payment.global.exception;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.genieus.common.response.ApiResponse;
import lombok.Builder;
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

  @ExceptionHandler(PaymentJsonParsingException.class)
  public ResponseEntity<ApiResponse<ErrorDetail>> handlePaymentJsonParsingException(PaymentJsonParsingException e) {
    log.warn("[JSON 파싱] {}", e.getMessage());

    ErrorDetail errorDetail = ErrorDetail.builder()
        .errorCode("JSON_PARSE_ERROR")
        .message(e.getCause().getOriginalMessage())
        .location(buildLocationString(e.getCause()))
        .build();

    return ResponseEntity.status(PaymentErrorCode.JSON_PARSING_ERROR.getHttpStatus())
        .body(ApiResponse.of(
            PaymentErrorCode.JSON_PARSING_ERROR.getCode(),
            PaymentErrorCode.JSON_PARSING_ERROR.getMessage(),
            errorDetail
        ));
  }

  @ExceptionHandler(PaymentJsonMappingException.class)
  public ResponseEntity<ApiResponse<ErrorDetail>> handlePaymentJsonMappingException(PaymentJsonMappingException e) {
    log.warn("[JSON 매핑] {}", e.getMessage());

    String fieldPath = buildFieldPath(e.getCause());

    ErrorDetail errorDetail = ErrorDetail.builder()
        .errorCode("JSON_MAPPING_ERROR")
        .message(String.format("필드 '%s' 매핑 실패: %s", fieldPath, e.getCause().getOriginalMessage()))
        .location(buildLocationString(e.getCause()))
        .build();

    return ResponseEntity.status(PaymentErrorCode.JSON_MAPPING_FAILED.getHttpStatus())
        .body(ApiResponse.of(
            PaymentErrorCode.JSON_MAPPING_FAILED.getCode(),
            PaymentErrorCode.JSON_MAPPING_FAILED.getMessage(),
            errorDetail
        ));
  }

  private String buildLocationString(JsonProcessingException e) {
    if (e.getLocation() == null) {
      return "Unknown location";
    }
    return String.format("line: %d, column: %d",
        e.getLocation().getLineNr(),
        e.getLocation().getColumnNr());
  }

  private String buildFieldPath(InvalidFormatException e) {
    if (e.getPath() == null || e.getPath().isEmpty()) {
      return "Unknown field";
    }

    StringBuilder fieldPath = new StringBuilder();
    for (com.fasterxml.jackson.databind.JsonMappingException.Reference ref : e.getPath()) {
      if (ref.getFieldName() != null) {
        fieldPath.append(ref.getFieldName()).append(".");
      }
    }

    if (!fieldPath.isEmpty()) {
      fieldPath.setLength(fieldPath.length() - 1);
    }

    return !fieldPath.isEmpty() ? fieldPath.toString() : "Unknown field";
  }

  @Builder
  public record ErrorDetail(String errorCode, String message, String location) {}
}
