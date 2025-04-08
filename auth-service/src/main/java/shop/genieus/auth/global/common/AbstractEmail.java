package shop.genieus.auth.global.common;

import lombok.Getter;

@Getter
public abstract class AbstractEmail {
  private static final int MAX_LENGTH = 20;
  private static final String EMAIL_REGEX =
      "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";

  protected String value;

  protected void validate(String value) {
    if (value == null || value.isBlank()) {
      throw new IllegalArgumentException("이메일은 필수 입력값입니다");
    }

    if (!value.matches(EMAIL_REGEX)) {
      throw new IllegalArgumentException(
          "이메일 형식이 올바르지 않습니다. 이메일은 영문자, 숫자, 특수문자(_+&*-)로 구성된 아이디와 도메인으로 구성되어야 합니다.");
    }

    if (value.length() > MAX_LENGTH) {
      throw new IllegalArgumentException("이메일은 " + MAX_LENGTH + "자를 초과할 수 없습니다");
    }
  }
}
