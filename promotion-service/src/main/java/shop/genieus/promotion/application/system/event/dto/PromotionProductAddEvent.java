package shop.genieus.promotion.application.system.event.dto;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class PromotionProductAddEvent extends ApplicationEvent {

  private final Long productId;
  private final Long promotionId;
  private final Integer discountRate;

  public PromotionProductAddEvent(Object source,
      Long productId, Long promotionId, Integer discountRate) {
    super(source);
    this.productId = productId;
    this.promotionId = promotionId;
    this.discountRate = discountRate;
  }
}
