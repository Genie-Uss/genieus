package shop.genieus.order.presentation.rest.dto.response;

import shop.genieus.order.domain.model.entity.Order;

public record CreateOrderResponse(Long orderId) {
  public static CreateOrderResponse toResponse(Order order) {
    return new CreateOrderResponse(order.getOrderId());
  }
}
