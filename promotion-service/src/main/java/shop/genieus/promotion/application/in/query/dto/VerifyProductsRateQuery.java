package shop.genieus.promotion.application.in.query.dto;

import java.time.LocalDateTime;
import java.util.List;

public record VerifyProductsRateQuery(
    List<PromotionItem> items,
    LocalDateTime orderedAt
) {
  public record PromotionItem(
      Long productId,
      Long promotionId
  ) {

  }
}
