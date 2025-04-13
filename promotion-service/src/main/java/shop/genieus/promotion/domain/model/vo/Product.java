package shop.genieus.promotion.domain.model.vo;

public record Product(
    Long productId,
    Long promotionId,
    Integer discountRate
) {
}
