package shop.genieus.promotion.infrastructure.persistence;

import static shop.genieus.promotion.global.constants.Code.*;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import shop.genieus.promotion.application.out.persistence.PromotionCommandPort;
import shop.genieus.promotion.domain.model.entity.Promotion;
import shop.genieus.promotion.global.exception.PromotionException;
import shop.genieus.promotion.infrastructure.persistence.repository.PromotionJpaRepository;

@Component
@RequiredArgsConstructor
public class PromotionCommandAdapter implements PromotionCommandPort {

  private final PromotionJpaRepository promotionJpaRepository;

  @Override
  public Promotion findById(Long Id) {
    return promotionJpaRepository.findById(Id)
        .orElseThrow(() -> new PromotionException(PROMOTION_NOT_FOUND));
  }
}
