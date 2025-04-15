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

  /**
   * 사용자가 자신의 주문을 취소합니다.
   *
   * 주문 요청자의 ID가 주문 소유자와 일치하지 않거나, 주문 상태가 사용자 취소를 허용하지 않으면 예외가 발생합니다.
   *
   * @param order 취소할 주문
   * @param userId 주문 취소를 요청하는 사용자 ID
   * @param now 취소 시각
   * @throws AccessOrderForbiddenException 주문 소유자가 아닌 사용자가 취소를 시도할 때 발생
   * @throws CancelOrderBadRequestException 주문 상태가 사용자 취소를 허용하지 않을 때 발생
   */
  public void cancelOrderByUser(Order order, Long userId, LocalDateTime now) {
    if (!order.getUserId().equals(userId)) {
      throw new AccessOrderForbiddenException();
    }
    if (!order.getStatus().canCancelByUser()) {
      throw new CancelOrderBadRequestException();
    }
    order.cancel(now);
  }

  /**
   * 주문 상태와 만료 시간을 기준으로 시스템에 의해 주문을 자동 취소합니다.
   *
   * 주문이 ORDER_PENDING 또는 PAYMENT_PENDING 상태인 경우, 각각 설정된 만료 시간이 지나지 않았다면 예외를 발생시킵니다.
   * 그 외의 상태에서는 시스템 취소가 허용되지 않으면 예외를 발생시킵니다.
   * 조건을 만족하면 주문을 취소하고 자동 취소 로그를 남깁니다.
   *
   * @throws CancelOrderBadRequestException 주문이 아직 만료되지 않았거나 시스템 취소가 허용되지 않은 상태인 경우
   */
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
