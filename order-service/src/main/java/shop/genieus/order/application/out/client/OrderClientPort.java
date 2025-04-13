package shop.genieus.order.application.out.client;

import java.time.LocalDateTime;
import java.util.List;
import shop.genieus.order.domain.model.assembler.OrderProductAssembler;
import shop.genieus.order.domain.model.entity.Order;
import shop.genieus.order.domain.model.vo.Coupon;
import shop.genieus.order.domain.model.vo.Product;
import shop.genieus.order.domain.model.vo.PromotionProduct;

public interface OrderClientPort {
  List<PromotionProduct> verifyPromotion(
      List<OrderProductAssembler> orderProductAssemblers, LocalDateTime orderedAt);

  List<Product> useStock(List<OrderProductAssembler> orderProductAssemblers);

  Coupon useCoupon(Long userId, Long couponId, LocalDateTime orderedAt);

  void createPayment(Order order);
}
