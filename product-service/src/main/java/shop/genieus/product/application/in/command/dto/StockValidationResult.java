package shop.genieus.product.application.in.command.dto;

import shop.genieus.product.domain.model.ProductView;

public record StockValidationResult(Long productId, String name, String text, Integer price) {
  public static StockValidationResult from(ProductView view) {
    return new StockValidationResult(
        view.getProductId(), view.getProductName(), view.getProductText(), view.getProductPrice());
  }
}
