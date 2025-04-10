package shop.genieus.order.presentation.rest.dto.response;

import shop.genieus.order.domain.model.entity.Order;

public record PaymentResponse(Long orderId) {
  public static PaymentResponse toResponse(Order order) {
    return new PaymentResponse(order.getOrderId());
  }
}
