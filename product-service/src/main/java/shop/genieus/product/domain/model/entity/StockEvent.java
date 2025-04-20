package shop.genieus.product.domain.model.entity;

import shop.genieus.product.domain.model.vo.StockEventStatus;
import shop.genieus.product.domain.model.vo.StockEventType;

public record StockEvent(
    Long productId,
    Long orderId,
    Integer quantity,
    Long timestamp,
    StockEventType type,
    StockEventStatus status) {
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

  public StockEvent markAsCompleted() {
    return new StockEvent(
        this.productId,
        this.orderId,
        this.quantity,
        this.timestamp,
        this.type,
        StockEventStatus.SUCCESS);
  }

  public StockEvent markAsFailed() {
    return new StockEvent(
        this.productId,
        this.orderId,
        this.quantity,
        this.timestamp,
        this.type,
        StockEventStatus.FAILED);
  }
}
