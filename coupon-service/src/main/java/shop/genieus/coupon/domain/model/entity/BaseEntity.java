package shop.genieus.coupon.domain.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import java.time.LocalDateTime;
import lombok.Getter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
@Getter
public class BaseEntity {

  @CreatedDate
  @Column(updatable = false)
  private LocalDateTime createdAt;

  @Column(updatable = false)
  private Long createdBy;

  @LastModifiedDate
  @Column(insertable = false)
  private LocalDateTime updatedAt;

  @LastModifiedBy
  @Column(insertable = false)
  private Long updatedBy;

  private LocalDateTime deletedAt;

  private Long deletedBy;

  public void delete(Long deletedBy) {
    this.deletedAt = LocalDateTime.now();
    this.deletedBy = deletedBy;
  }
}
