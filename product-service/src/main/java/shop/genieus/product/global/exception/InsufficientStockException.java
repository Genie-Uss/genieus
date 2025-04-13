package shop.genieus.product.global.exception;

import lombok.Getter;

@Getter
public class InsufficientStockException extends ProductException {
  private static final String INSUFFICIENT_STOCK = "상품을 찾을 수 없거나 판매 상태 중이 아닙니다.";
  private static final int INSUFFICIENT_STOCK_CODE = 2002;

  private InsufficientStockException(String additionalMessage) {
    super(INSUFFICIENT_STOCK + ", " + additionalMessage, INSUFFICIENT_STOCK_CODE);
  }

  private InsufficientStockException() {
    super(INSUFFICIENT_STOCK, INSUFFICIENT_STOCK_CODE);
  }

  public static InsufficientStockException create() {
    return new InsufficientStockException();
  }

  public static InsufficientStockException create(String additionalMessage) {
    return new InsufficientStockException(additionalMessage);
  }
}
