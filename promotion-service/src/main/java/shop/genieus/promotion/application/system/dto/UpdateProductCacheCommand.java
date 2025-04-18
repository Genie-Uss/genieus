package shop.genieus.promotion.application.system.dto;

public record UpdateProductCacheCommand(
    Long productId,
    Long promotionId,
    Integer discountRate
) {
}
