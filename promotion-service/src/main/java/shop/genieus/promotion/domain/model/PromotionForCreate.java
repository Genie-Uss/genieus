package shop.genieus.promotion.domain.model;

import java.time.LocalDateTime;
import shop.genieus.promotion.domain.model.vo.PromotionStatus;

public record PromotionForCreate(
    String promotionName,
    LocalDateTime promotionStartDate,
    LocalDateTime promotionEndDate,
    PromotionStatus promotionStatus
) {

}
