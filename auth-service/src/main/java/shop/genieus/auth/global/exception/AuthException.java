package shop.genieus.auth.global.exception;

import lombok.Getter;

@Getter
public class AuthException extends RuntimeException {
  private static final int DEFAULT_CODE = 7999;
  private final int code;

  protected AuthException(String message, int code) {
    super(message);
    this.code = code;
  }

  protected AuthException(String message) {
    this(message, DEFAULT_CODE);
  }

  protected AuthException(String message, Throwable cause, int code) {
    super(message, cause);
    this.code = code;
  }

  protected AuthException(String message, Throwable cause) {
    this(message, cause, DEFAULT_CODE);
  }
}
