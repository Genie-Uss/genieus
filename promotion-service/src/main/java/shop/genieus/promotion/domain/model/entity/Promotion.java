package shop.genieus.promotion.domain.model.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;
import shop.genieus.promotion.domain.model.vo.PromotionStatus;

@Getter
@Entity
@Comment("프로모션 테이블")
@Table(name = "m_promotion")
@Builder(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Promotion extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long promotionId;

  @Column(nullable = false, length = 50)
  @Comment("프로모션 이름")
  private String promotionName;

  @Column(nullable = false)
  @Comment("프로모션 시작 날짜")
  private LocalDateTime promotionStartDate;

  @Column
  @Comment("프로모션 종료 날짜")
  private LocalDateTime promotionEndDate;

  @Column(nullable = false)
  @Enumerated(value = EnumType.STRING)
  @Comment("프로모션 상태")
  private PromotionStatus promotionStatus;

  @Builder.Default
  @OneToMany(mappedBy = "promotion", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<PromotionProduct> promotionProducts = new ArrayList<>();

  public static Promotion create(
      String promotionName,
      LocalDateTime promotionStartDate,
      LocalDateTime promotionEndDate,
      PromotionStatus promotionStatus,
      List<PromotionProduct> promotionProducts
      ) {
    return Promotion.builder()
        .promotionName(promotionName)
        .promotionStartDate(promotionStartDate)
        .promotionEndDate(promotionEndDate)
        .promotionStatus(promotionStatus)
        .promotionProducts(promotionProducts)
        .build();
  }
}
