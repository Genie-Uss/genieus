package shop.genieus.order.domain.service;

import java.util.List;
import lombok.extern.slf4j.Slf4j;
import shop.genieus.order.domain.model.assembler.CreateOrderAssembler;
import shop.genieus.order.domain.model.assembler.OrderProductAssembler;
import shop.genieus.order.domain.model.entity.Order;
import shop.genieus.order.domain.model.vo.Coupon;

@Slf4j
public class OrderPriceCalculator {

  public static void calculate(CreateOrderAssembler assembler) {
    List<OrderProductAssembler> orderProductAssemblers = assembler.getOrderProducts();
    int totalProductPrice = 0;
    int totalDiscountAmount = 0;

    for (OrderProductAssembler product : orderProductAssemblers) {
      int price = product.getProductPrice();
      int quantity = product.getQuantity();
      int discountRate = product.getPromotionDiscountRate();

      int productPrice = price * quantity;
      int discountAmount = (productPrice * discountRate) / 100;

      totalProductPrice += productPrice;
      totalDiscountAmount += discountAmount;
    }
    assembler.applyTotalProductPrice(totalProductPrice);
    assembler.applyPromotionDiscountAmount(totalDiscountAmount);
  }

  public static Integer useCoupon(Order order, Coupon coupon) {
    int discountRate = coupon.couponDiscountRate();
    int maxPrice = coupon.couponMaxPrice();
    int finalPrice = order.getOrderPrice().getFinalPrice();
    int couponDiscountAmount = finalPrice * discountRate / 100;
    return Math.min(couponDiscountAmount, maxPrice);
  }
}
