package shop.genieus.promotion.infrastructure.persistence.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import shop.genieus.promotion.domain.model.entity.Promotion;

@Repository
public interface PromotionJpaRepository extends JpaRepository<Promotion, Long> {

  Optional<Promotion> findByPromotionIdAndDeletedAtIsNull(Long promotionId);
}
