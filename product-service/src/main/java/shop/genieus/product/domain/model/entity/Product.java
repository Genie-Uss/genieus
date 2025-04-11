package shop.genieus.product.domain.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;
import shop.genieus.product.domain.model.vo.ProductPrice;
import shop.genieus.product.domain.model.vo.ProductStatus;

@Getter
@Entity
@Comment("상품 테이블")
@Table(name = "m_product")
@Builder(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Product extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long productId;

  @Column(nullable = false, length = 30)
  @Comment("상품 이름")
  private String productName;

  @Comment("상품 설명")
  private String productText;

  @Embedded
  @Comment("상품 가격")
  private ProductPrice productPrice;

  @Column(nullable = false)
  @Comment("상품 재고")
  private Integer productTotalStock;

  @Column(nullable = false)
  @Comment("상품 상태")
  private ProductStatus productStatus;

  public static Product create(
      String productName,
      String productText,
      Integer productPrice,
      Integer productTotalStock,
      ProductStatus productStatus
  ) {
    return Product.builder()
        .productName(productName)
        .productText(productText)
        .productPrice(ProductPrice.of(productPrice))
        .productTotalStock(productTotalStock)
        .productStatus(productStatus)
        .build();
  }
}
