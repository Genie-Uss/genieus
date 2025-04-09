package shop.genieus.promotion.application.out.persistence;

import shop.genieus.promotion.domain.model.entity.Promotion;

public interface PromotionCommandPort {

  Promotion findById(Long Id);
}
