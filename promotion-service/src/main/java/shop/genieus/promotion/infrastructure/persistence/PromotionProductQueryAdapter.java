package shop.genieus.promotion.infrastructure.persistence;

import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import shop.genieus.promotion.application.out.persistence.PromotionProductQueryPort;
import shop.genieus.promotion.domain.model.entity.PromotionProduct;
import shop.genieus.promotion.infrastructure.persistence.repository.PromotionProductQueryRepository;

@Component
@RequiredArgsConstructor
public class PromotionProductQueryAdapter implements PromotionProductQueryPort {

  private final PromotionProductQueryRepository jpaRepository;

  @Override
  public List<PromotionProduct> findMaxDiscountRateProductsByDate(LocalDateTime date) {
    return jpaRepository.findMaxDiscountRateProductsByDate(date);
  }
}
