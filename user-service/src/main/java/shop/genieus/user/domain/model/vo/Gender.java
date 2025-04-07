package shop.genieus.user.domain.model.vo;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Gender {
  MALE("남성"),
  FEMALE("여성");

  private final String koreanName;
  private final String englishName = this.name();
}
