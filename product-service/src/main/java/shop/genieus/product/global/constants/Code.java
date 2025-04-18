package shop.genieus.product.global.constants;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Code {
  CREATE_PRODUCT_SUCCESS(2101, "상품이 생성되었습니다."),
  FIND_PRODUCT_SUCCESS(2102, "상품이 조회되었습니다."),
  ;

  private final Integer code;
  private final String message;
}
