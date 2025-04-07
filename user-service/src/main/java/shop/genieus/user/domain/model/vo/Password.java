package shop.genieus.user.domain.model.vo;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import shop.genieus.user.domain.model.service.PasswordEncryptionService;

@Getter
@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Password {
  private static final int MIN_LENGTH = 8;
  private static final int MAX_LENGTH = 20;
  private static final String PASSWORD_PATTERN =
      "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[!@#$%^&*()_+\\-=\\[\\]{}|;:,.<>?])(.{"
          + MIN_LENGTH
          + ","
          + MAX_LENGTH
          + "})$";

  @Column(name = "password", length = 255, nullable = false)
  private String value;

  private Password(String value) {
    this.value = value;
  }

  public static Password of(
      String plainPassword, PasswordEncryptionService passwordEncryptionService) {
    validatePassword(plainPassword);
    return new Password(passwordEncryptionService.encode(plainPassword));
  }

  private static void validatePassword(String plainPassword) {
    if (plainPassword == null || plainPassword.isBlank()) {
      throw new IllegalArgumentException("비밀번호는 필수 입력값입니다");
    }

    if (!plainPassword.matches(PASSWORD_PATTERN)) {
      throw new IllegalArgumentException(
          "비밀번호는 " + MIN_LENGTH + "~" + MAX_LENGTH + "자 사이의 대문자, 소문자, 숫자, 특수문자를 모두 포함해야 합니다");
    }
  }
}
