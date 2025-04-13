package shop.genieus.product.application.in.command.dto;

import java.util.List;

public record ValidateProductCommand(List<StockValidationItem> validateProductStocks) {
  public record StockValidationItem(Long id, Integer quantity) {}
}
