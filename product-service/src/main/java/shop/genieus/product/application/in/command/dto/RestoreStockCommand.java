package shop.genieus.product.application.in.command.dto;

import java.time.LocalDateTime;
import java.util.List;

public record RestoreStockCommand(
    Long orderId, List<RestoreStockItem> items, LocalDateTime canceledAt) {
  public record RestoreStockItem(Long productId, Integer quantity) {}
}
