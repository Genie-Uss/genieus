package shop.genieus.user.global.exception;

public class PasswordMismatchException extends UserException {
  private static final String PASSWORD_MISMATCH = "비밀번호가 일치하지 않습니다.";
  private static final int PASSWORD_MISMATCH_CODE = 1002;

  public PasswordMismatchException() {
    super(PASSWORD_MISMATCH, PASSWORD_MISMATCH_CODE);
  }
}