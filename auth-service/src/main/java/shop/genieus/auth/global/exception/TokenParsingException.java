package shop.genieus.auth.global.exception;

public class TokenParsingException extends AuthException {
  private static final String INVALID_SIGNATURE = "유효하지 않는 서명입니다.";
  private static final int INVALID_SIGNATURE_CODE = 7002;

  private static final String UNSUPPORTED_TOKEN = "지원되지 않는 토큰입니다.";
  private static final int UNSUPPORTED_TOKEN_CODE = 7003;

  private static final String MALFORMED_TOKEN = "잘못된 형식의 토큰입니다.";
  private static final int MALFORMED_TOKEN_CODE = 7004;

  private static final String EMPTY_CLAIMS = "토큰의 클레임이 비어있습니다.";
  private static final int EMPTY_CLAIMS_CODE = 7005;

  private static final String GENERAL_ERROR = "토큰 처리 중 오류가 발생했습니다.";
  private static final int GENERAL_ERROR_CODE = 7006;

  private TokenParsingException(String message, int code) {
    super(message);
  }

  public static TokenParsingException invalidSignature() {
    return new TokenParsingException(INVALID_SIGNATURE, INVALID_SIGNATURE_CODE);
  }

  public static TokenParsingException unsupportedToken() {
    return new TokenParsingException(UNSUPPORTED_TOKEN, UNSUPPORTED_TOKEN_CODE);
  }

  public static TokenParsingException malformedToken() {
    return new TokenParsingException(MALFORMED_TOKEN, MALFORMED_TOKEN_CODE);
  }

  public static TokenParsingException emptyClaims() {
    return new TokenParsingException(EMPTY_CLAIMS, EMPTY_CLAIMS_CODE);
  }

  public static TokenParsingException generalError() {
    return new TokenParsingException(GENERAL_ERROR, GENERAL_ERROR_CODE);
  }
}
