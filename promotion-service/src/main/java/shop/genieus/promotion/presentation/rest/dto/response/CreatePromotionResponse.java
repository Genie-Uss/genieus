package shop.genieus.promotion.presentation.rest.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDateTime;
import java.util.List;
import shop.genieus.promotion.domain.model.entity.Promotion;
import shop.genieus.promotion.domain.model.vo.PromotionProductStatus;
import shop.genieus.promotion.domain.model.vo.PromotionStatus;

public record CreatePromotionResponse(
    Long promotionId,
    String promotionName,
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    LocalDateTime promotionStartDate,
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    LocalDateTime promotionEndDate,
    PromotionStatus promotionStatus,
    List<PromotionProduct> promotionProducts
) {

  public static CreatePromotionResponse from(Promotion promotion) {
    List<PromotionProduct> promotionProducts =
        promotion.getPromotionProducts()
            .stream()
            .map(PromotionProduct::fromEntity)
            .toList();

    return new CreatePromotionResponse(
        promotion.getPromotionId(),
        promotion.getPromotionName(), 
        promotion.getPromotionStartDate(),
        promotion.getPromotionEndDate(),
        promotion.getPromotionStatus(),
        promotionProducts
    );
  }

  public record PromotionProduct(
      Long promotionProductId,
      Long productId,
      Integer promotionProductDisCountRate,
      PromotionProductStatus promotionProductStatus
  ) {
    public static PromotionProduct fromEntity(
        shop.genieus.promotion.domain.model.entity.PromotionProduct promotionProduct) {

      return new PromotionProduct(
          promotionProduct.getPromotionProductId(),
          promotionProduct.getProductId(),
          promotionProduct.getPromotionProductDiscountRate().getValue(),
          promotionProduct.getPromotionProductStatus()
      );
    }
  }
}
