package shop.genieus.product.presentation.rest.dto.response;

import shop.genieus.product.domain.model.entity.Product;
import shop.genieus.product.domain.model.vo.ProductStatus;

public record CreateProductResponse(
    Long productId,
    String productName,
    String productText,
    Integer productPrice,
    Integer productTotalStock,
    ProductStatus productStatus
) {
  public static CreateProductResponse from(Product product) {
    return new CreateProductResponse(
        product.getProductId(),
        product.getProductName(),
        product.getProductText(),
        product.getProductPrice().getValue(),
        product.getProductTotalStock(),
        product.getProductStatus()
    );
  }
}
