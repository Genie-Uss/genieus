package shop.genieus.auth.domain.model.vo;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import shop.genieus.auth.domain.model.service.PasswordEncryptionService;

@Getter
@Embeddable
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Password {

  @Column(name = "password", length = 255, nullable = false)
  private String value;

  public static Password of(String hashedPassword) {
    validateHashedPassword(hashedPassword);
    return new Password(hashedPassword);
  }

  private static void validateHashedPassword(String hashedPassword) {
    if (hashedPassword == null || hashedPassword.isBlank()) {
      throw new IllegalArgumentException("비밀번호는 필수 입력값입니다");
    }
  }

  public boolean matches(
      String plainPassword, PasswordEncryptionService passwordEncryptionService) {
    return passwordEncryptionService.matches(plainPassword, this.value);
  }
}
