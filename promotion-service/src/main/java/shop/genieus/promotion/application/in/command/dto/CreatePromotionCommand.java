package shop.genieus.promotion.application.in.command.dto;

import java.time.LocalDateTime;
import java.util.List;
import shop.genieus.promotion.domain.model.vo.PromotionProductStatus;
import shop.genieus.promotion.domain.model.vo.PromotionStatus;

public record CreatePromotionCommand(
    String promotionName,
    LocalDateTime promotionStartDate,
    LocalDateTime promotionEndDate,
    PromotionStatus promotionStatus,
    List<PromotionProductDto> promotionProducts
) {
  public record PromotionProductDto(
      Long productId,
      Integer promotionProductDiscountRate,
      PromotionProductStatus promotionProductStatus) {}
}
