package shop.genieus.product.presentation.event.mapper;

import com.genieus.common.event.order.OrderCanceledEvent;
import com.genieus.common.event.order.OrderCompletedEvent;
import com.genieus.common.event.order.OrderExpiredEvent;
import java.util.List;
import org.springframework.stereotype.Component;
import shop.genieus.product.application.in.command.dto.RestoreStockCommand;
import shop.genieus.product.application.in.command.dto.RestoreStockCommand.RestoreStockItem;
import shop.genieus.product.application.system.dto.OrderCompletedCommand;

@Component
public class ProductEventMapper {
  public RestoreStockCommand toRestoreStockCommand(OrderCanceledEvent event) {
    List<RestoreStockItem> items =
        event.orderProductItems().stream()
            .map(item -> new RestoreStockItem(item.productId(), item.quantity()))
            .toList();

    return new RestoreStockCommand(event.orderId(), items, event.canceledAt());
  }

  public RestoreStockCommand toRestoreStockCommand(OrderExpiredEvent event) {
    List<RestoreStockItem> items =
        event.orderProductItems().stream()
            .map(item -> new RestoreStockItem(item.productId(), item.quantity()))
            .toList();

    return new RestoreStockCommand(event.orderId(), items, event.expiredAt());
  }

  public OrderCompletedCommand toOrderCompletedCommand(OrderCompletedEvent event) {
    List<OrderCompletedCommand.OrderProductItem> commandItem =
        event.orderProductItems().stream()
            .map(
                item ->
                    new OrderCompletedCommand.OrderProductItem(item.productId(), item.quantity()))
            .toList();

    return new OrderCompletedCommand(event.orderId(), event.completedAt(), commandItem);
  }
}
