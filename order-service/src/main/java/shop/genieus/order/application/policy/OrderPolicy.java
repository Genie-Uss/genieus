package shop.genieus.order.application.policy;

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
public class OrderPolicy {

  @Value("${order.cancel-policy.order-pending:20}")
  private long orderPendingMinutes;

  public LocalDateTime calculateOrderExpiration(LocalDateTime orderedAt) {
    return orderedAt.plusMinutes(orderPendingMinutes);
  }

  public void cancelOrder(Order order, Long userId, LocalDateTime now) {
    if (!order.getUserId().equals(userId)) {
      throw new AccessOrderForbiddenException();
    }
    if (!order.getStatus().canCancel()) {
      throw new CancelOrderBadRequestException();
    }
    order.cancel(now);
  }

  public boolean expireByOrderDeadline(Order order, LocalDateTime now) {
    if (order.getStatus() != OrderStatus.ORDER_PENDING) {
      log.warn(
          "[expireByOrderDeadline] 만료 스킵 - 잘못된 상태: orderId={}, status={}",
          order.getOrderId(),
          order.getStatus());
      return false;
    }
    LocalDateTime deadline = order.getOrderTimeStamp().getOrderDeadlineAt();
    if (now.isBefore(deadline)) {
      log.info(
          "[expireByOrderDeadline] 만료 스킵 - 아직 기한 전: orderId={}, deadline={}",
          order.getOrderId(),
          deadline);
      return false;
    }
    order.expire(now);
    return true;
  }
}
