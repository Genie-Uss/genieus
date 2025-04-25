package shop.genieus.product.application.in.command.dto;

import java.time.LocalDateTime;
import java.util.List;

public record OrderCompletedCommand(
        Long orderId,
        LocalDateTime completedAt,
        List<OrderProductItem> orderProductItems
) {
    public record OrderProductItem(
            Long productId,
            Integer quantity
    ) {
    }
}
