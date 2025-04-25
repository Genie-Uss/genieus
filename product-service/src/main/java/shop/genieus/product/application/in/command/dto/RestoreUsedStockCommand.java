package shop.genieus.product.application.in.command.dto;

import java.util.List;

public record RestoreUsedStockCommand(Long orderId, List<RestoreStockItem> items) {}
