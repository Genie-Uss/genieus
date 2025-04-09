package shop.genieus.promotion.domain.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;
import shop.genieus.promotion.domain.model.vo.PromotionProductStatus;
import shop.genieus.promotion.domain.model.vo.Rate;

@Getter
@Entity
@Comment("프로모션 상품 테이블")
@Table(name = "m_promotion_product")
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class PromotionProduct extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long promotionProductId;

  @Column(nullable = false)
  @Comment("상품 아이디")
  private Long productId;

  @Embedded
  @Comment("프로모션 상품 할인율")
  private Rate promotionProductDiscountRate;

  @Column(nullable = false)
  @Comment("할인가")
  private Integer promotionProductDiscountPrice;

  @Column(nullable = false)
  @Enumerated(value = EnumType.STRING)
  @Comment("프로모션 상품 상태")
  private PromotionProductStatus promotionProductStatus;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "promotion_id")
  private Promotion promotion;
}
