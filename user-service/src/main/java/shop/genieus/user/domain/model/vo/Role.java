package shop.genieus.user.domain.model.vo;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Embeddable
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Role {
  @Column(name = "role", nullable = false)
  @Enumerated(EnumType.STRING)
  private RoleType value;

  private Role(RoleType value) {
    validateRole(value);
    this.value = value;
  }

  public static Role of(RoleType value) {
    return new Role(value);
  }

  private void validateRole(RoleType value) {
    if (value == null) {
      throw new IllegalArgumentException("권한은 필수 입력값입니다");
    }
  }
}
