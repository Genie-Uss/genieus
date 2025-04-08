package shop.genieus.auth.global.common;

import lombok.Getter;

@Getter
public abstract class AbstractRole {
  protected RoleType value;

  protected void validateRole(RoleType value) {
    if (value == null) {
      throw new IllegalArgumentException("권한은 필수 입력값입니다");
    }
  }
}
