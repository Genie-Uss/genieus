package shop.genieus.product.domain.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import shop.genieus.product.domain.model.entity.Product;
import shop.genieus.product.domain.model.vo.ProductStatus;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ProductView {
  private Long productId;
  private String productName;
  private String productText;
  private ProductStatus productStatus;
  private Integer productPrice;
  private Long productTotalStock;

  public static ProductView from(Product product) {
    return ProductView.builder()
        .productId(product.getProductId())
        .productName(product.getProductName().getValue())
        .productText(product.getProductText())
        .productStatus(product.getProductStatus())
        .productPrice(product.getProductPrice().getValue())
        .productTotalStock((long) product.getProductTotalStock())
        .build();
  }

  @JsonIgnore
  public boolean isOnSale() {
    return productStatus == ProductStatus.ON_SALE;
  }
}
