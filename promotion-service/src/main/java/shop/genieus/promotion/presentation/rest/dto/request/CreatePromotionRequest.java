package shop.genieus.promotion.presentation.rest.dto.request;


import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.List;
import shop.genieus.promotion.application.in.command.dto.CreatePromotionCommand;
import shop.genieus.promotion.application.in.command.dto.CreatePromotionCommand.PromotionProductDto;
import shop.genieus.promotion.domain.model.vo.PromotionProductStatus;
import shop.genieus.promotion.domain.model.vo.PromotionStatus;

public record CreatePromotionRequest(
    @NotBlank(message = "프로모션명은 필수입니다.")
    @Size(max = 50, message = "프로모션명은 50자 이하로 입력해주세요.")
    String promotionName,
    @NotNull(message = "프로모션 시작 날짜는 필수로 입력해야합니다.")
    LocalDateTime promotionStartDate,
    LocalDateTime promotionEndDate,
    PromotionStatus promotionStatus,
    @Valid List<PromotionProduct> promotionProducts
) {

  public CreatePromotionCommand toCommand() {
    List<PromotionProductDto> promotionProductDtoList
        = this.promotionProducts().stream()
        .map(PromotionProduct::promotionProductToCommand)
        .toList();

    return new CreatePromotionCommand(
        promotionName(),
        promotionStartDate(),
        promotionEndDate(),
        promotionStatus(),
        promotionProductDtoList
    );
  }

  public record PromotionProduct(
      @NotNull(message = "상품은 필수로 입력해야합니다.")
      Long productId,
      @NotNull(message = "할인율은 필수로 입력해야합니다.")
      @Min(value = 1, message = "할인율은 1 이상이어야 합니다.")
      @Max(value = 99, message = "할인율은 99이하여야 합니다.")    
      Integer promotionProductDiscountRate,
      PromotionProductStatus promotionProductStatus
  ) {
    public PromotionProductDto promotionProductToCommand() {
      return new PromotionProductDto(
          productId(),
          promotionProductDiscountRate(),
          promotionProductStatus()
      );
    }
  }
}
