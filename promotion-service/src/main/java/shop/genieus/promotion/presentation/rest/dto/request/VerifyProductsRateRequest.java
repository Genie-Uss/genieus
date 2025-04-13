package shop.genieus.promotion.presentation.rest.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.format.annotation.DateTimeFormat;
import shop.genieus.promotion.application.in.query.dto.VerifyProductsRateQuery;

public record VerifyProductsRateRequest(
    @Valid List<PromotionItem> items,
    @NotNull(message = "주문 날짜는 필수입니다.")
    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    LocalDateTime orderedAt
) {

  public VerifyProductsRateQuery toCommand() {
    List<VerifyProductsRateQuery.PromotionItem> itemList
        = this.items().stream()
        .map(PromotionItem::promotionItemToCommand)
        .toList();

    return new VerifyProductsRateQuery(
        itemList,
        orderedAt()
    );
  }

  public record PromotionItem(
      @NotNull(message = "상품은 필수로 입력해야합니다.") Long productId,
      @NotNull(message = "프로모션은 필수로 입력해야합니다.") Long promotionId
  ) {
    public VerifyProductsRateQuery.PromotionItem promotionItemToCommand() {
      return new VerifyProductsRateQuery.PromotionItem(
          productId(),
          promotionId()
      );
    }
  }
}
