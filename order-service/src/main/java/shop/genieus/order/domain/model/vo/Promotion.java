package shop.genieus.order.domain.model.vo;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;

@Getter
@Embeddable
@EqualsAndHashCode
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Promotion {

  @Column(name = "promotion_id", nullable = false)
  @Comment("프로모션 식별자")
  private Long promotionId;

  @Column(name = "promotion_discount_rate", nullable = false)
  @Comment("프로모션 할인율")
  private Integer promotionDiscountRate;

  private Promotion(Long id, Integer discountRate) {
    validate(id, discountRate);
    this.promotionId = id;
    this.promotionDiscountRate = discountRate;
  }

  public static Promotion of(Long promotionId, Integer promotionDiscountRate) {
    if (promotionDiscountRate == null) {
      promotionDiscountRate = 0;
    }
    return new Promotion(promotionId, promotionDiscountRate);
  }

  private void validate(Long promotionId, Integer promotionDiscountRate) {

    if (promotionId == null) {
      if (promotionDiscountRate != null) {
        throw new IllegalArgumentException("프로모션이 유효하지 않습니다.");
      }
    }

    if (promotionId != null) {
      if (promotionDiscountRate == null) {
        throw new IllegalArgumentException("프로모션의 할인율이 설정되지 않았습니다.");
      }
      if (promotionDiscountRate < 0 || promotionDiscountRate > 100) {
        throw new IllegalArgumentException("할인율은 0과 100사이여야 합니다.");
      }
    }
  }
}
