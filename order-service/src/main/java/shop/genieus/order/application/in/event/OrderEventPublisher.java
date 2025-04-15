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

  /**
   * 주문 생성 이벤트를 발행합니다.
   *
   * @param order 생성된 주문 정보
   */
  public void publishOrderCreated(Order order) {
    OrderCreatedEvent event = OrderCreatedEvent.of(order);
    publisher.publishEvent(event);
  }

  /**
   * 주문이 취소되었을 때 해당 정보를 담은 도메인 이벤트를 발행합니다.
   *
   * @param order 취소된 주문 엔티티
   */
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

  /**
   * 주문이 완료되었음을 나타내는 도메인 이벤트를 발행합니다.
   *
   * @param order 완료된 주문 정보
   */
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

  /**
   * OrderProduct 엔티티 목록을 각 상품의 ID와 수량 정보를 담은 OrderProductItem DTO 목록으로 변환합니다.
   *
   * @param orderProducts 변환할 OrderProduct 엔티티 리스트
   * @return 각 상품의 ID와 수량이 포함된 OrderProductItem 리스트
   */
  private List<OrderProductItem> toOrderProductItems(List<OrderProduct> orderProducts) {
    return orderProducts.stream()
        .map(
            op ->
                new OrderProductItem(
                    op.getProduct().getProductId(), op.getQuantity().getQuantity()))
        .toList();
  }
}
