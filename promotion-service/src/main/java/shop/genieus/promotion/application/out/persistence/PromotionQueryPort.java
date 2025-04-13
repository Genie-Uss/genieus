package shop.genieus.promotion.application.out.persistence;

import java.util.List;
import shop.genieus.promotion.application.in.query.dto.VerifyProductsRateQuery;
import shop.genieus.promotion.domain.model.entity.PromotionProduct;
import shop.genieus.promotion.domain.model.vo.Product;

public interface PromotionQueryPort {

  List<Product> verifyProductDiscountRate(VerifyProductsRateQuery query);
  void saveProductDiscountRate(List<PromotionProduct> promotionProducts);

}
