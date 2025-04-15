package shop.genieus.order.application.policy;

import static shop.genieus.order.domain.model.vo.OrderStatus.*;
import static shop.genieus.order.global.exception.CustomBadRequestException.*;
import static shop.genieus.order.global.exception.CustomForbiddenException.*;

import java.time.LocalDateTime;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import shop.genieus.order.domain.model.entity.Order;
import shop.genieus.order.domain.model.vo.OrderStatus;

@Slf4j
@Getter
@Component
@RequiredArgsConstructor
public class OrderCancelPolicy {

  @Value("${order.cancel-policy.order-pending:20}")
  private long orderPendingMinutes;

  @Value("${order.cancel-policy.payment-pending:10}")
  private long paymentPendingMinutes;

  public void cancelOrderByUser(Order order, Long userId, LocalDateTime now) {
    if (!order.getUserId().equals(userId)) {
      throw new AccessOrderForbiddenException();
    }
    if (!order.getStatus().canCancelByUser()) {
      throw new CancelOrderBadRequestException();
    }
    order.cancel(now);
  }

  public void cancelOrderBySystem(Order order, LocalDateTime now) {
    OrderStatus status = order.getStatus();

    switch (status) {
      case ORDER_PENDING -> {
        LocalDateTime orderExpireAt = order.getOrderedAt().plusMinutes(orderPendingMinutes);
        if (now.isBefore(orderExpireAt)) {
          throw new CancelOrderBadRequestException();
        }
      }
      case PAYMENT_PENDING -> {
        LocalDateTime paymentExpireAt =
            order.getPaymentRequestedAt().plusMinutes(paymentPendingMinutes);
        if (now.isBefore(paymentExpireAt)) {
          throw new CancelOrderBadRequestException();
        }
      }
      default -> {
        if (!status.canCancelBySystem()) {
          throw new CancelOrderBadRequestException();
        }
      }
    }
    order.cancel(now);
    log.info("[cancelOrderBySystem] 주문시간이 만료되어 자동 취소되었습니다. orderId:{}", order.getOrderId());
  }
}
