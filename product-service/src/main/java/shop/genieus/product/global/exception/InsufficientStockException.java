package shop.genieus.product.global.exception;

import lombok.Getter;

@Getter
public class InsufficientStockException extends ProductException {
  private static final String INSUFFICIENT_STOCK = "상품의 재고가 부족합니다.";
  private static final int INSUFFICIENT_STOCK_CODE = 2002;

  public InsufficientStockException() {
    super(INSUFFICIENT_STOCK, INSUFFICIENT_STOCK_CODE);
  }
}
