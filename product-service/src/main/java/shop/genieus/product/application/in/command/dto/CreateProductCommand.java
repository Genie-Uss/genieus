package shop.genieus.product.application.in.command.dto;

import shop.genieus.product.domain.model.vo.ProductStatus;

public record CreateProductCommand(
    String productName,
    String productText,
    Integer productPrice,
    Integer productTotalStock,
    ProductStatus productStatus
) {
}
