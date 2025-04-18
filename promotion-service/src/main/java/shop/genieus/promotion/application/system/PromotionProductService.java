package shop.genieus.promotion.application.system;

import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import shop.genieus.promotion.application.out.persistence.PromotionProductQueryPort;
import shop.genieus.promotion.application.out.persistence.PromotionQueryPort;
import shop.genieus.promotion.application.system.dto.UpdateProductCacheCommand;
import shop.genieus.promotion.domain.model.entity.PromotionProduct;

@Slf4j
@Service
@RequiredArgsConstructor
public class PromotionProductService {

  private final PromotionProductQueryPort promotionProductQueryPort; // jpa
  private final PromotionQueryPort promotionQueryPort; // redis

  public void findMaxDiscountRateProductsByDate(LocalDateTime date) {
    List<PromotionProduct> promotionProducts =
        promotionProductQueryPort.findMaxDiscountRateProductsByDate(date);
    log.info("조회된 상품 개수 : {}", promotionProducts.size());

    promotionQueryPort.saveProductDiscountRate(promotionProducts, date);
  }

  public void updateProductDiscountRate(UpdateProductCacheCommand command) {
    String hashField = command.productId() + ":" + command.promotionId();
    promotionQueryPort.updateProductDiscountRate(hashField, command.discountRate());
    log.info("개별 상품 상시 판매 업데이트 : hashField: {}, rate: {}",
        hashField, command.discountRate());
  }
}
