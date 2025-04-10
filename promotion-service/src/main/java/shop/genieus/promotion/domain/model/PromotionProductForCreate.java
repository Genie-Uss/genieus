package shop.genieus.promotion.domain.model;

import shop.genieus.promotion.domain.model.vo.PromotionProductStatus;

public record PromotionProductForCreate(
    Long productId,
    Integer promotionProductDiscountRate,
    PromotionProductStatus promotionProductStatus) {}
