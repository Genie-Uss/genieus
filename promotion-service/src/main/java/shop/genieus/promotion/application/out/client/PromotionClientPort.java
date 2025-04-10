package shop.genieus.promotion.application.out.client;

import java.util.List;

public interface PromotionClientPort {
  List<Long> findProducts(List<Long> productIds);
}
