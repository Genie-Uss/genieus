package shop.genieus.product.domain.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import java.time.LocalDateTime;
import lombok.Getter;
import org.hibernate.annotations.Comment;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Getter
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public abstract class BaseEntity {

  @CreatedDate
  @Comment("생성 일시")
  @Column(name = "created_at", nullable = false, updatable = false)
  private LocalDateTime createdAt;

  @CreatedBy
  @Comment("생성자")
  @Column(name = "created_by", nullable = false, updatable = false)
  private Long createdBy;

  @LastModifiedDate
  @Comment("수정 일시")
  @Column(name = "updated_at")
  private LocalDateTime updatedAt;

  @LastModifiedBy
  @Comment("수정자")
  @Column(name = "updated_by", insertable = false)
  private Long updatedBy;

  @Comment("삭제 일시")
  @Column(name = "deleted_at")
  private LocalDateTime deletedAt;

  @Comment("삭제자")
  @Column(name = "deleted_by")
  private Long deletedBy;

  public void delete(Long id) {
    this.deletedAt = LocalDateTime.now();
    this.deletedBy = id;
  }
}
