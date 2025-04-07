package shop.genieus.user.domain.model.vo;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Embeddable
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Name {
  private static final int MAX_LENGTH = 5;

  @Column(name = "name", length = MAX_LENGTH, nullable = false)
  private String value;

  private Name(String value) {
    validateName(value);
    this.value = value;
  }

  public static Name of(String value) {
    return new Name(value);
  }

  private void validateName(String name) {
    if (name == null || name.isBlank()) {
      throw new IllegalArgumentException("이름은 필수 입력값입니다");
    }

    if (name.length() > MAX_LENGTH) {
      throw new IllegalArgumentException("이름은 " + MAX_LENGTH + "자 이내여야 합니다");
    }
  }
}
