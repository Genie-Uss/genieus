package shop.genieus.auth.global.exception;

public class TokenExpiredException extends RuntimeException {
  private static final String EXPIRED_TOKEN = "만료된 JWT 토큰입니다.";

  public TokenExpiredException(String message) {
    super(message);
  }

  public static TokenExpiredException create() {
    return new TokenExpiredException(EXPIRED_TOKEN);
  }
}
