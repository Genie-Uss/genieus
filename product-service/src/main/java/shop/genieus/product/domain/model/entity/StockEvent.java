package shop.genieus.product.domain.model.entity;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import shop.genieus.product.domain.model.vo.StockEventStatus;
import shop.genieus.product.domain.model.vo.StockEventType;

@Getter
@ToString
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class StockEvent {
  private Long productId;
  private Long orderId;
  private Integer quantity;
  private Long timestamp;
  private StockEventType type;
  private StockEventStatus status;

  public static StockEvent createDecreaseEvent(
      Long productId, Long orderId, Integer quantity, Long timestamp) {
    return new StockEvent(
        productId, orderId, quantity, timestamp, StockEventType.DECREASE, StockEventStatus.PENDING);
  }

  public static StockEvent createIncreaseEvent(
      Long productId, Long orderId, Integer quantity, Long timestamp) {
    return new StockEvent(
        productId, orderId, quantity, timestamp, StockEventType.INCREASE, StockEventStatus.PENDING);
  }

  public void markAsCompleted() {
    this.status = StockEventStatus.SUCCESS;
  }

  public void markAsFailed() {
    this.status = StockEventStatus.FAILED;
  }
}
