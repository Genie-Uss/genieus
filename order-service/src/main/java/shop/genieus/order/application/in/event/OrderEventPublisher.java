package shop.genieus.order.application.in.event;

import com.genieus.common.event.order.OrderCanceledEvent;
import com.genieus.common.event.order.OrderCanceledEvent.OrderProductItem;
import com.genieus.common.event.order.OrderCompletedEvent;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import shop.genieus.order.domain.event.OrderCreatedEvent;
import shop.genieus.order.domain.model.entity.Order;
import shop.genieus.order.domain.model.entity.OrderProduct;

@Component
@RequiredArgsConstructor
public class OrderEventPublisher {

  private final ApplicationEventPublisher publisher;

  public void publishOrderCreated(Order order) {
    OrderCreatedEvent event = OrderCreatedEvent.of(order);
    publisher.publishEvent(event);
  }

  public void publishOrderCanceled(Order order) {
    OrderCanceledEvent event =
        new OrderCanceledEvent(
            order.getOrderId(),
            order.getUserId(),
            order.getCouponId(),
            toOrderProductItems(order.getOrderProducts()),
            order.getCanceledAt());
    publisher.publishEvent(event);
  }

  public void publishOrderCompleted(Order order) {
    OrderCompletedEvent event =
        new OrderCompletedEvent(
            order.getOrderId(),
            order.getUserId(),
            order.getCouponId(),
            new ArrayList<>(),
            order.getCanceledAt());
    publisher.publishEvent(event);
  }

  private List<OrderProductItem> toOrderProductItems(List<OrderProduct> orderProducts) {
    return orderProducts.stream()
        .map(
            op ->
                new OrderProductItem(
                    op.getProduct().getProductId(), op.getQuantity().getQuantity()))
        .toList();
  }
}
