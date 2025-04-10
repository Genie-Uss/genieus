package shop.genieus.order.domain.model.entity;

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
  @Column(name = "created_at", nullable = false, updatable = false)
  @Comment("생성 일시")
  private LocalDateTime createdAt;

  @CreatedBy
  @Column(name = "created_by", nullable = false, updatable = false)
  @Comment("생성자")
  private Long createdBy;

  @LastModifiedDate
  @Column(name = "updated_at", nullable = false)
  @Comment("수정 일시")
  private LocalDateTime updatedAt;

  @LastModifiedBy
  @Column(name = "updated_by", nullable = false)
  @Comment("수정자")
  private Long updatedBy;

  @Column(name = "is_deleted", nullable = false)
  @Comment("삭제여부")
  private boolean isDeleted;

  @Column(name = "deleted_at")
  @Comment("삭제 일시")
  private LocalDateTime deletedAt;

  @Column(name = "deleted_by")
  @Comment("삭제자")
  private Long deletedBy;

  protected void createdByUser(Long userId) {
    this.createdAt = LocalDateTime.now();
    this.createdBy = userId;
    this.updatedAt = LocalDateTime.now();
    this.updatedBy = userId;
  }

  protected void createdBySystem() {
    this.createdAt = LocalDateTime.now();
    this.createdBy = 0L;
    this.updatedAt = LocalDateTime.now();
    this.updatedBy = 0L;
  }

  protected void updatedByUser(Long userId) {
    this.updatedAt = LocalDateTime.now();
    this.updatedBy = userId;
  }

  protected void updatedBySystem() {
    this.updatedAt = LocalDateTime.now();
    this.updatedBy = 0L;
  }

  protected void deletedByUser(Long userId) {
    this.deletedAt = LocalDateTime.now();
    this.deletedBy = userId;
  }

  protected void deleteBySystem() {
    this.deletedAt = LocalDateTime.now();
    this.deletedBy = 0L;
  }
}
