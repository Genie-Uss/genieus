package shop.genieus.order.infrastructure.event.internal;

import com.genieus.common.event.order.CouponRestoredEvent;
import com.genieus.common.event.order.OrderCanceledEvent;
import com.genieus.common.event.order.OrderCompletedEvent;
import com.genieus.common.event.order.OrderCreationFailedEvent;
import com.genieus.common.event.order.OrderExpiredEvent;
import com.genieus.common.event.order.OrderPaymentRequestedEvent;
import com.genieus.common.event.order.OrderProductItem;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import shop.genieus.order.application.in.command.dto.CreateOrderCommand;
import shop.genieus.order.application.in.command.dto.CreateOrderCommand.OrderProductCommand;
import shop.genieus.order.application.out.event.OrderInternalEventPort;
import shop.genieus.order.domain.event.OrderCreatedEvent;
import shop.genieus.order.domain.model.entity.Order;
import shop.genieus.order.domain.model.entity.OrderProduct;

@Component
@RequiredArgsConstructor
public class OrderInternalEventAdapter implements OrderInternalEventPort {

  private final ApplicationEventPublisher publisher;

  @Override
  public void publishOrderCreated(Order order) {
    OrderCreatedEvent event = OrderCreatedEvent.of(order);
    publisher.publishEvent(event);
  }

  @Override
  public void publishOrderCanceled(Order order) {
    OrderCanceledEvent event =
        new OrderCanceledEvent(
            order.getOrderId(),
            order.getUserId(),
            order.getCouponId(),
            fromOrderProduct(order.getOrderProducts()),
            order.getOrderTimeStamp().getOrderCanceledAt());
    publisher.publishEvent(event);
  }

  @Override
  public void publishOrderCompleted(Order order) {
    OrderCompletedEvent event =
        new OrderCompletedEvent(
            order.getOrderId(),
            order.getUserId(),
            order.getCouponId(),
            fromOrderProduct(order.getOrderProducts()),
            order.getOrderTimeStamp().getOrderCompletedAt());
    publisher.publishEvent(event);
  }

  @Override
  public void publishOrderExpired(Order order) {
    OrderExpiredEvent event =
        new OrderExpiredEvent(
            order.getOrderId(),
            order.getUserId(),
            fromOrderProduct(order.getOrderProducts()),
            order.getOrderTimeStamp().getOrderExpiredAt());
    publisher.publishEvent(event);
  }

  @Override
  public void publishPaymentRequested(Order order) {
    OrderPaymentRequestedEvent event =
        new OrderPaymentRequestedEvent(
            order.getOrderId(),
            order.getOrderPrice().getFinalPrice(),
            order.getOrderTimeStamp().getPaymentRequestedAt());
    publisher.publishEvent(event);
  }

  @Override
  public void publishCouponUsed(Order order) {
    CouponRestoredEvent event =
        new CouponRestoredEvent(order.getOrderId(), order.getUserId(), order.getCouponId());
    publisher.publishEvent(event);
  }

  @Override
  public void publishStockReserved(CreateOrderCommand command) {
    OrderCreationFailedEvent event =
        new OrderCreationFailedEvent(fromOrderProductCommand(command.orderProductCommands()));
    publisher.publishEvent(event);
  }

  private List<OrderProductItem> fromOrderProduct(List<OrderProduct> orderProducts) {
    return orderProducts.stream()
        .map(
            op ->
                new OrderProductItem(
                    op.getProduct().getProductId(), op.getQuantity().getQuantity()))
        .toList();
  }

  private List<OrderProductItem> fromOrderProductCommand(List<OrderProductCommand> orderProducts) {
    return orderProducts.stream()
        .map(op -> new OrderProductItem(op.productId(), op.quantity()))
        .toList();
  }
}
