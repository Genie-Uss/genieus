package shop.genieus.promotion.global.constants;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Code {

  CREATE_PROMOTION_SUCCESS(3101, "프로모션이 생성되었습니다."),
  PROMOTION_NOT_FOUND(3001, "프로모션이 존재하지 않습니다.");

  private final Integer code;
  private final String message;
}
