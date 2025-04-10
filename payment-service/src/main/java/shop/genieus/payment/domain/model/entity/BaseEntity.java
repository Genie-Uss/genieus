package shop.genieus.payment.domain.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import org.hibernate.annotations.Comment;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Getter
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public class BaseEntity {

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

    public void delete() {
        deletedAt = LocalDateTime.now();
    }
}
