package shop.genieus.promotion.global.constants;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Code {

  CREATE_PROMOTION_SUCCESS(3101, "프로모션이 생성되었습니다."),
  ALREADY_PROMOTION_PRODUCT(3002, "이미 존재하는 프로모션 상품 입니다."),
  PROMOTION_NOT_FOUND(3001, "프로모션이 존재하지 않습니다."),
  REDIS_DATE_NOT_FOUND(3030, "주문 날짜 데이터가 존재하지 않습니다."),
  REDIS_RATE_NOT_FOUND(3031, "주문 날짜의 특정 상품이 존재하지 않습니다. "),
  ;

  private final Integer code;
  private final String message;
}
