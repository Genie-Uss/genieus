package shop.genieus.product.presentation.event.mapper;

import com.genieus.common.event.order.OrderCanceledEvent;
import java.util.List;
import org.springframework.stereotype.Component;
import shop.genieus.product.application.in.command.dto.RestoreStockCommand;
import shop.genieus.product.application.in.command.dto.RestoreStockCommand.RestoreStockItem;

@Component
public class ProductEventMapper {
  public RestoreStockCommand toRestoreStockCommand(OrderCanceledEvent event) {
    List<RestoreStockItem> items =
        event.orderProductItems().stream()
            .map(item -> new RestoreStockItem(item.productId(), item.quantity()))
            .toList();

    return new RestoreStockCommand(event.orderId(), items, event.canceledAt());
  }
}
