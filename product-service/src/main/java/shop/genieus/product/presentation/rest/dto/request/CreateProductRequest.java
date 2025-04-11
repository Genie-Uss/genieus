package shop.genieus.product.presentation.rest.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import shop.genieus.product.application.in.command.dto.CreateProductCommand;
import shop.genieus.product.domain.model.vo.ProductStatus;

public record CreateProductRequest(
    @NotBlank(message = "상품명은 필수입니다.")
    @Size(max = 30, message = "상품명은 30자 이하로 입력해주세요.")
    String productName,
    String productText,
    @NotNull(message = "가격은 필수로 입력해야합니다.")
    @Min(value = 100, message = "가격은 100원이상만 입력할 수 있습니다.")
    @Max(value = 100000000, message = "가격은 1억을 넘을 수 없습니다.")
    Integer productPrice,
    @Positive(message = "재고는 1개 이상 입력해야합니다.")
    Integer productTotalStock,
    @NotNull(message = "상품 상태는 필수로 입력해야합니다.")
    ProductStatus productStatus
) {

  public CreateProductCommand toCommand() {
    return new CreateProductCommand(
        productName(),
        productText(),
        productPrice(),
        productTotalStock(),
        productStatus()
    );
  }
}
