package shop.genieus.promotion.application.out.persistence;

import shop.genieus.promotion.application.in.command.dto.CreatePromotionCommand;
import shop.genieus.promotion.domain.model.entity.Promotion;

public interface PromotionCommandPort {

  Promotion save(CreatePromotionCommand command);
  Promotion findById(Long promotionId);
  Promotion findByName(String promotionName);
  boolean existByDefaultPromotionProduct(Long productId);
}
