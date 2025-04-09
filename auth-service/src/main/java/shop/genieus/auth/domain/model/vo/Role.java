package shop.genieus.auth.domain.model.vo;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import shop.genieus.auth.global.common.AbstractRole;
import shop.genieus.auth.global.common.RoleType;

@Getter
@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Role extends AbstractRole {

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
}
