package shop.genieus.order.domain.event;

import com.genieus.common.event.DomainEvent;
import java.time.LocalDateTime;
import java.util.List;
import shop.genieus.order.domain.model.entity.Order;
import shop.genieus.order.domain.model.entity.OrderProduct;
import shop.genieus.order.domain.model.vo.OrderPrice;
import shop.genieus.order.domain.model.vo.OrderStatus;

public record OrderCreatedEvent(
    Long orderId,
    Long userid,
    List<OrderProductDto> orderProducts,
    LocalDateTime orderedAt,
    OrderStatus status,
    OrderPriceDto orderPrice)
    implements DomainEvent {
  public record OrderProductDto(Long productId, Integer quantity) {
    public static OrderProductDto of(OrderProduct orderProduct) {
      return new OrderProductDto(
          orderProduct.getProduct().getProductId(), orderProduct.getQuantity().getQuantity());
    }
  }

  public record OrderPriceDto(
      Integer totalProductPrice,
      Integer promotionDiscountAmount,
      Integer couponDiscountAmount,
      Integer totalDiscountAmount,
      Integer finalPrice) {
    public static OrderPriceDto of(OrderPrice orderPrice) {
      return new OrderPriceDto(
          orderPrice.getTotalProductPrice(),
          orderPrice.getPromotionDiscountAmount(),
          orderPrice.getCouponDiscountAmount(),
          orderPrice.getTotalDiscountAmount(),
          orderPrice.getFinalPrice());
    }
  }

  public static OrderCreatedEvent of(Order order) {
    return new OrderCreatedEvent(
        order.getOrderId(),
        order.getUserId(),
        order.getOrderProducts().stream().map(OrderProductDto::of).toList(),
        order.getOrderedAt(),
        order.getStatus(),
        OrderPriceDto.of(order.getOrderPrice()));
  }
}
