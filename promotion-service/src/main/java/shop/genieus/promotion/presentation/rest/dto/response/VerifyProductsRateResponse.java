package shop.genieus.promotion.presentation.rest.dto.response;

import shop.genieus.promotion.domain.model.vo.Product;

public record VerifyProductsRateResponse(
    Long productId,
    Long promotionId,
    Integer discountRate
) {
  public static VerifyProductsRateResponse from(Product product) {
    return new VerifyProductsRateResponse(
        product.productId(),
        product.promotionId(),
        product.discountRate()
    );
  }
}
