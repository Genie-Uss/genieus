package shop.genieus.auth.global.exception;

public class TokenParsingException extends RuntimeException {
  private static final String INVALID_SIGNATURE = "유효하지 않는 서명입니다.";
  private static final String UNSUPPORTED_TOKEN = "지원되지 않는 토큰입니다.";
  private static final String MALFORMED_TOKEN = "잘못된 형식의 토큰입니다.";
  private static final String EMPTY_CLAIMS = "토큰의 클레임이 비어있습니다.";
  private static final String GENERAL_ERROR = "토큰 처리 중 오류가 발생했습니다.";

  private TokenParsingException(String message) {
    super(message);
  }

  public static TokenParsingException invalidSignature() {
    return new TokenParsingException(INVALID_SIGNATURE);
  }

  public static TokenParsingException unsupportedToken() {
    return new TokenParsingException(UNSUPPORTED_TOKEN);
  }

  public static TokenParsingException malformedToken() {
    return new TokenParsingException(MALFORMED_TOKEN);
  }

  public static TokenParsingException emptyClaims() {
    return new TokenParsingException(EMPTY_CLAIMS);
  }

  public static TokenParsingException generalError() {
    return new TokenParsingException(GENERAL_ERROR);
  }
}
