package shop.genieus.payment.infrastructure.out.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;

@Entity
@Table(name = "payment_outbox")
@Getter @Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Outbox {

  @Id
  @Comment("결제 아웃박스 테이블 식별자")
  @Column(name = "payment_outbox_id")
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long paymentOutboxId;

  @Lob
  @Comment("이벤트 객체")
  @Column(name = "event", nullable = false)
  private String event;

  @Comment("아웃박스 발행 여부")
  @Column(name = "is_published", nullable = false)
  private Boolean isPublished = false;

  public void markPublished() {
    this.isPublished = true;
  }
}
