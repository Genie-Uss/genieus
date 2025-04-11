package shop.genieus.order.application.in.command;

import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import shop.genieus.order.application.in.command.dto.*;
import shop.genieus.order.application.out.client.OrderClientPort;
import shop.genieus.order.application.out.persistence.OrderCommandPort;
import shop.genieus.order.application.out.util.OrderTimePort;
import shop.genieus.order.domain.model.assembler.CreateOrderAssembler;
import shop.genieus.order.domain.model.entity.Order;
import shop.genieus.order.domain.model.vo.Coupon;
import shop.genieus.order.domain.model.vo.Product;
import shop.genieus.order.domain.model.vo.PromotionProduct;
import shop.genieus.order.domain.service.OrderCancelPolicy;
import shop.genieus.order.domain.service.OrderPriceCalculator;

@Service
@Transactional
@RequiredArgsConstructor
public class OrderCommandService {
  private final OrderTimePort orderTimePort;
  private final OrderClientPort orderClientPort;
  private final OrderCommandPort orderCommandPort;

  public Order create(CreateOrderCommand command) {

    CreateOrderAssembler assembler = command.toAssembler();
    assembler.applyOrderedAt(getCurrentTime());

    List<PromotionProduct> promotionProducts = getPromotionProducts(assembler);
    applyPromotionDiscounts(assembler, promotionProducts);

    List<Product> products = getProducts(assembler);
    applyProductPrices(assembler, products);

    OrderPriceCalculator.calculate(assembler);

    Order order = Order.create(assembler);
    return orderCommandPort.save(order);
  }

  public Order requestPayment(PaymentCommand command) {
    LocalDateTime paymentRequested = getCurrentTime();
    Order order = findOrder(command.orderId());

    processCouponForPayment(command, order);

    order.requestPayment(paymentRequested);
    return order;
  }

  public void cancelOrderByUser(CancelOrderCommand command) {
    LocalDateTime canceledAt = getCurrentTime();
    Order order = findOrder(command.orderId());
    OrderCancelPolicy.cancelOrderByUser(order, command.userId(), canceledAt);
  }

  public void cancelOrderBySystem(CancelOrderCommand command) {
    LocalDateTime canceledAt = getCurrentTime();
    Order order = findOrder(command.orderId());
    OrderCancelPolicy.cancelOrderBySystem(order, canceledAt);
  }

  public void completePayment(CompletePaymentCommand command) {
    LocalDateTime paidAt = getCurrentTime();
    Order order = findOrder(command.orderId());
    order.completePayment(paidAt);
  }

  public void completeOrder(CompleteOrderCommand command) {
    LocalDateTime completedAt = getCurrentTime();
    Order order = findOrder(command.orderId());
    order.completeOrder(completedAt);
  }

  private Order findOrder(Long orderId) {
    return orderCommandPort.findById(orderId);
  }

  private LocalDateTime getCurrentTime() {
    return orderTimePort.now();
  }

  private void processCouponForPayment(PaymentCommand command, Order order) {
    if (command.couponId() != null) {
      Coupon coupon = getCoupon(command, order);
      Integer couponDiscountAmount = OrderPriceCalculator.useCoupon(order, coupon);
      order.useCoupon(couponDiscountAmount);
    }
  }

  private Coupon getCoupon(PaymentCommand command, Order order) {
    return orderClientPort.useCoupon(order.getUserId(), command.couponId(), order.getOrderedAt());
  }

  private List<PromotionProduct> getPromotionProducts(CreateOrderAssembler assembler) {
    return orderClientPort.verifyPromotion(assembler.getOrderProducts(), assembler.getOrderedAt());
  }

  private List<Product> getProducts(CreateOrderAssembler assembler) {
    return orderClientPort.useStock(assembler.getOrderProducts());
  }

  private void applyPromotionDiscounts(
      CreateOrderAssembler assembler, List<PromotionProduct> promotionProducts) {
    promotionProducts.forEach(
        pp ->
            assembler.getOrderProducts().stream()
                .filter(op -> op.getProductId().equals(pp.productId()))
                .forEach(op -> op.applyDiscountRate(pp.discountRate())));
  }

  private void applyProductPrices(CreateOrderAssembler assembler, List<Product> products) {
    products.forEach(
        p ->
            assembler.getOrderProducts().stream()
                .filter(op -> op.getProductId().equals(p.productId()))
                .forEach(op -> op.applyProductPrice(p.productPrice())));
  }
}
