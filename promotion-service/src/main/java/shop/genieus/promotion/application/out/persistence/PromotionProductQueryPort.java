package shop.genieus.promotion.application.out.persistence;

import java.time.LocalDateTime;
import java.util.List;
import shop.genieus.promotion.domain.model.entity.PromotionProduct;

public interface PromotionProductQueryPort {

  List<PromotionProduct> findMaxDiscountRateProductsByDate(LocalDateTime date);
}
