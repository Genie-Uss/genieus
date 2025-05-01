package shop.genieus.product.domain.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;
import shop.genieus.product.domain.model.vo.StockEventType;

@Getter
@Entity
@Comment("재고 히스토리 테이블")
@Table(name = "m_stock_history")
@Builder(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class StockHistory {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long stockHistoryId;

  @Column(name = "product_id", nullable = false)
  @Comment("상품 ID")
  private Long productId;

  @Column(nullable = false)
  @Comment("주문 ID")
  private Long orderId;

  @Column(nullable = false)
  @Comment("변동량")
  private Integer quantity;

  @Column(nullable = false)
  @Comment("재고 요청 타입")
  @Enumerated(EnumType.STRING)
  private StockEventType type;

  @Column(nullable = false)
  @Comment("주문 처리 시간")
  private LocalDateTime processedAt;

  @Column(nullable = false)
  @Comment("생성 시간")
  private LocalDateTime createdAt;

  public static StockHistory create(
      Long productId,
      Long orderId,
      Integer quantity,
      StockEventType type,
      LocalDateTime processedAt) {
    return StockHistory.builder()
        .productId(productId)
        .orderId(orderId)
        .quantity(quantity)
        .type(type)
        .processedAt(processedAt)
        .createdAt(LocalDateTime.now())
        .build();
  }
}
