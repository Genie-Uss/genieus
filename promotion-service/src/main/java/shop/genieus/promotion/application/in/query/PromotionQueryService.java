package shop.genieus.promotion.application.in.query;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import shop.genieus.promotion.application.in.query.dto.VerifyProductsRateQuery;
import shop.genieus.promotion.application.out.persistence.PromotionQueryPort;
import shop.genieus.promotion.domain.model.vo.Product;

@Service
@RequiredArgsConstructor
public class PromotionQueryService {

  private final PromotionQueryPort promotionQueryPort;

  public List<Product> verifyRates(VerifyProductsRateQuery query) {
    return promotionQueryPort.verifyProductDiscountRate(query);
  }
}
