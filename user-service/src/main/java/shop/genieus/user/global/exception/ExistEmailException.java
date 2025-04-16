package shop.genieus.user.global.exception;

public class ExistEmailException extends UserException {
  private static final String EXIST_EMAIL = "중복된 이메일입니다.";
  private static final int EXIST_EMAIL_CODE = 1001;

  public ExistEmailException() {
    super(EXIST_EMAIL, EXIST_EMAIL_CODE);
  }
}
