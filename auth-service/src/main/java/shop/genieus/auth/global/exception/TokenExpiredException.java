package shop.genieus.auth.global.exception;

public class TokenExpiredException extends AuthException {
  private static final String EXPIRED_TOKEN = "만료된 JWT 토큰입니다.";
  private static final int TOKEN_EXPIRED_CODE = 7001;

  public TokenExpiredException(String message) {
    super(message, TOKEN_EXPIRED_CODE);
  }

  public static TokenExpiredException create() {
    return new TokenExpiredException(EXPIRED_TOKEN);
  }
}
