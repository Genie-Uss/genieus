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
    /**
     * OrderProduct 엔티티를 OrderProductDto로 변환합니다.
     *
     * @param orderProduct 변환할 주문 상품 엔티티
     * @return 주문 상품 정보를 담은 DTO
     */
    public static OrderProductDto of(OrderProduct orderProduct) {
      return new OrderProductDto(
          orderProduct.getProduct().getProductId(), orderProduct.getQuantity().getQuantity());
    }
  }

  public record OrderPriceDto(
      Integer totalProductPrice,
      Integer PromotionDiscountAmount,
      Integer couponDiscountAmount,
      Integer totalDiscountAmount,
      Integer finalPrice) {
    /**
     * OrderPrice 객체를 OrderPriceDto로 변환합니다.
     *
     * @param orderPrice 변환할 주문 가격 정보
     * @return 주문 가격 정보를 담은 OrderPriceDto 인스턴스
     */
    public static OrderPriceDto of(OrderPrice orderPrice) {
      return new OrderPriceDto(
          orderPrice.getTotalProductPrice(),
          orderPrice.getPromotionDiscountAmount(),
          orderPrice.getCouponDiscountAmount(),
          orderPrice.getTotalDiscountAmount(),
          orderPrice.getFinalPrice());
    }
  }

  /**
   * Order 엔티티로부터 OrderCreatedEvent 인스턴스를 생성합니다.
   *
   * @param order 주문 정보를 담고 있는 Order 엔티티
   * @return 주문 생성 이벤트를 나타내는 OrderCreatedEvent 객체
   */
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
