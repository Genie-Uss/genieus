package shop.genieus.order.domain.service;

import static shop.genieus.order.domain.model.vo.OrderStatus.*;
import static shop.genieus.order.global.exception.CustomBadRequestException.*;
import static shop.genieus.order.global.exception.CustomForbiddenException.*;

import java.time.LocalDateTime;
import shop.genieus.order.domain.model.entity.Order;
import shop.genieus.order.domain.model.vo.OrderStatus;

public class OrderCancelPolicy {

  public static void cancelOrderByUser(Order order, Long userId, LocalDateTime canceledAt) {
    if (!order.getUserId().equals(userId)) {
      throw new AccessOrderForbiddenException();
    }
    if (!order.getStatus().canCancelByUser()) {
      throw new CancelOrderBadRequestException();
    }
    order.cancel(canceledAt);
  }

  public static void cancelOrderBySystem(Order order, LocalDateTime canceledAt) {
    OrderStatus status = order.getStatus();
    LocalDateTime orderedAt = order.getOrderedAt();
    LocalDateTime paymentRequestedAt = order.getPaymentRequestedAt();

    if (status == ORDER_PENDING && orderedAt.plusMinutes(20).isAfter(canceledAt)) {
      throw new CancelOrderBadRequestException();
    }
    if (status == PAYMENT_PENDING && paymentRequestedAt.plusMinutes(20).isAfter(canceledAt)) {
      throw new CancelOrderBadRequestException();
    }
    if (!status.canCancelBySystem()) {
      order.cancel(canceledAt);
    }
  }
}
