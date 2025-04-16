package shop.genieus.user.global.exception;

import lombok.Getter;

@Getter
public class UserException extends RuntimeException {
  private static final int DEFAULT_CODE = 1999;
  private final int code;

  public UserException(String message, int code) {
    super(message);
    this.code = code;
  }

  public UserException(String message) {
    this(message, DEFAULT_CODE);
  }

  public UserException(String message, Throwable cause, int code) {
    super(message, cause);
    this.code = code;
  }

  protected UserException(String message, Throwable cause) {
    this(message, cause, DEFAULT_CODE);
  }
}
