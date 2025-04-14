package shop.genieus.product.global.exception;

import lombok.Getter;

@Getter
public class ProductNotFoundException extends ProductException {
  private static final String PRODUCT_NOT_FOUND = "상품을 찾을 수 없거나 판매 상태 중이 아닙니다.";
  private static final int PRODUCT_NOT_FOUND_CODE = 2001;

  public ProductNotFoundException() {
    super(PRODUCT_NOT_FOUND, PRODUCT_NOT_FOUND_CODE);
  }
}
