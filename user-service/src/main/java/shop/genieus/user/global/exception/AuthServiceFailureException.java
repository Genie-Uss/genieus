package shop.genieus.user.global.exception;

public class AuthServiceFailureException extends UserException {
  private static final String AUTH_SERVICE_UNAVAILABLE = "인증 서비스 연결에 실패했습니다.";
  private static final int AUTH_SERVICE_UNAVAILABLE_CODE = 1003;

  public AuthServiceFailureException() {
    super(AUTH_SERVICE_UNAVAILABLE, AUTH_SERVICE_UNAVAILABLE_CODE);
  }
}
