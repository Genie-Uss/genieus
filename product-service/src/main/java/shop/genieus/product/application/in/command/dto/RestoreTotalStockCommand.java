package shop.genieus.product.application.in.command.dto;

import java.time.LocalDateTime;
import java.util.List;

public record RestoreTotalStockCommand(
    Long orderId, List<RestoreStockItem> items, LocalDateTime canceledAt) {}
