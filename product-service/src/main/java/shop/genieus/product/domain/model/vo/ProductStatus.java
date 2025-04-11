package shop.genieus.product.domain.model.vo;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ProductStatus {
  READY("판매 대기"),
  ON_SALE("판매 중"),
  SOLD_OUT("품절"),
  STOPPED("판매 중지"),
  END("판매 종료"),
  ;

  private final String description;
}
