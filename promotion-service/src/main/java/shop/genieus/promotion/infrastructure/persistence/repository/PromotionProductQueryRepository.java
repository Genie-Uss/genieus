package shop.genieus.promotion.infrastructure.persistence.repository;

import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import shop.genieus.promotion.domain.model.entity.PromotionProduct;

@Repository
public interface PromotionProductQueryRepository
    extends JpaRepository<PromotionProduct, Long> {

  @Query(value = """
        WITH ranked_promotions AS (
          SELECT
            pp.*,
            ROW_NUMBER() OVER (
              PARTITION BY pp.product_id 
              ORDER BY pp.promotion_product_discount_rate DESC, pp.promotion_product_id ASC
            ) AS rn
          FROM m_promotion_product pp
          JOIN m_promotion p ON pp.promotion_id = p.promotion_id
          WHERE :date BETWEEN p.promotion_start_date AND p.promotion_end_date
        )
        SELECT *
        FROM ranked_promotions
        WHERE rn = 1
        """, nativeQuery = true)
  List<PromotionProduct> findMaxDiscountRateProductsByDate(
      @Param("date") LocalDateTime date);
}
