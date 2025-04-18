package shop.genieus.product.domain.model;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import shop.genieus.product.domain.model.entity.Product;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ProductView {
  private Long productId;
  private String productName;
  private String productText;
  private Integer productPrice;

  public static ProductView from(Product product) {
    return ProductView.builder()
        .productId(product.getProductId())
        .productName(product.getProductName().getValue())
        .productText(product.getProductText())
        .productPrice(product.getProductPrice().getValue())
        .build();
  }
}
