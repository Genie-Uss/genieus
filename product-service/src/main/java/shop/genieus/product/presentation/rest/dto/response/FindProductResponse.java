package shop.genieus.product.presentation.rest.dto.response;

import shop.genieus.product.domain.model.ProductView;

public record FindProductResponse(
    Long productId, String productName, String productText, Integer productPrice) {
  public static FindProductResponse from(ProductView product) {
    return new FindProductResponse(
        product.getProductId(),
        product.getProductName(),
        product.getProductText(),
        product.getProductPrice());
  }
}
