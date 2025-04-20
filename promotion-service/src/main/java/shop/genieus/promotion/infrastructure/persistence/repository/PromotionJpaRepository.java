package shop.genieus.promotion.infrastructure.persistence.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import shop.genieus.promotion.domain.model.entity.Promotion;

@Repository
public interface PromotionJpaRepository extends JpaRepository<Promotion, Long> {

  Optional<Promotion> findByPromotionIdAndDeletedAtIsNull(Long promotionId);

  Optional<Promotion> findByPromotionNameAndDeletedAtIsNull(String promotionName);

  @Query("""
    SELECT CASE WHEN EXISTS (
        SELECT 1
        FROM Promotion p
        WHERE p.promotionName = :promotionName
          AND p.deletedAt IS NULL
          AND EXISTS (
            SELECT 1
            FROM PromotionProduct pp
            WHERE pp.promotion = p
              AND pp.productId = :productId
              AND pp.deletedAt IS NULL
          )
    ) THEN true ELSE false END
    """)
  boolean existByPromotionProductAndDeletedAtIsNull(
          @Param("promotionName") String promotionName,
          @Param("productId") Long productId);
}
