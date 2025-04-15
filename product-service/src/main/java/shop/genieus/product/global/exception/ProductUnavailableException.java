package shop.genieus.product.global.exception;

public class ProductUnavailableException extends ProductException {
  private static final String UNAVAILABLE_PRODUCT = "요청한 상품 중 판매 중이 아닌 상품이 존재합니다.";
  private static final int UNAVAILABLE_PRODUCT_CODE = 2003;

  public ProductUnavailableException() {
    super(UNAVAILABLE_PRODUCT, UNAVAILABLE_PRODUCT_CODE);
  }
}
