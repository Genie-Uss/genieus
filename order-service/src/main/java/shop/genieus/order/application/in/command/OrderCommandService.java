package shop.genieus.order.application.in.command;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import shop.genieus.order.application.in.command.dto.*;
import shop.genieus.order.application.out.client.OrderClientPort;
import shop.genieus.order.application.out.event.OrderInternalEventPort;
import shop.genieus.order.application.out.persistence.OrderCommandPort;
import shop.genieus.order.application.out.util.OrderTimePort;
import shop.genieus.order.application.policy.OrderPolicy;
import shop.genieus.order.domain.model.assembler.CreateOrderAssembler;
import shop.genieus.order.domain.model.assembler.OrderProductAssembler;
import shop.genieus.order.domain.model.entity.Order;
import shop.genieus.order.domain.model.entity.OrderProduct;
import shop.genieus.order.domain.model.vo.Coupon;
import shop.genieus.order.domain.model.vo.Product;
import shop.genieus.order.domain.model.vo.PromotionProduct;
import shop.genieus.order.domain.service.OrderPriceCalculator;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderCommandService {
  private final OrderTimePort timePort;
  private final OrderPolicy orderPolicy;
  private final OrderClientPort clientPort;
  private final OrderCommandPort commandPort;
  private final OrderInternalEventPort internalEventPort;
  private final OrderTransactionalSupport transactionalSupport;

  public Order create(CreateOrderCommand command) {
    CreateOrderAssembler assembler = command.toAssembler();

    LocalDateTime orderedAt = getCurrentTime();
    LocalDateTime orderDeadLineAt = orderPolicy.calculateOrderExpiration(orderedAt);
    assembler.applyOrderPeriod(orderedAt, orderDeadLineAt);

    List<OrderProductAssembler> productAssemblers = command.toProductAssembler();
    List<PromotionProduct> promotions = getPromotionProducts(productAssemblers, orderedAt);

    List<Product> products = getProducts(productAssemblers);
    try {
      applyPromotionDiscounts(productAssemblers, promotions);
      applyProductPrices(productAssemblers, products);
      List<OrderProduct> orderProducts =
          productAssemblers.stream().map(OrderProduct::create).toList();
      assembler.applyOrderProducts(orderProducts);
      OrderPriceCalculator.calculate(assembler);
      Order order = Order.create(assembler);
      return transactionalSupport.saveAndPublish(order);
    } catch (Exception e) {
      internalEventPort.publishOrderCreationFailed(command);
      throw e;
    }
  }

  @Transactional
  public Order requestPayment(PaymentCommand command) {
    LocalDateTime paymentRequestedAt = getCurrentTime();
    Order order = findOrder(command.orderId());
    processCouponForPayment(command, order);
    order.requestPayment(paymentRequestedAt);
    createPayment(order);
    internalEventPort.publishPaymentRequested(order);
    return order;
  }

  @Transactional
  public void cancelOrder(CancelOrderCommand command) {
    LocalDateTime canceledAt = getCurrentTime();
    Order order = findOrder(command.orderId());
    orderPolicy.cancelOrder(order, command.userId(), canceledAt);
    internalEventPort.publishOrderCanceled(order);
  }

  @Transactional
  public void expireOrders(ExpireOrderCommand command) {
    LocalDateTime expiredAt = getCurrentTime();
    List<Order> orders = findOrders(command.orderIds());
    List<Long> expiredOrderIds = new ArrayList<>();
    orders.forEach(
        order -> {
          boolean expired = orderPolicy.expireByOrderDeadline(order, expiredAt);
          if (expired) {
            internalEventPort.publishOrderExpired(order);
            expiredOrderIds.add(order.getOrderId());
          }
        });
    commandPort.expireAll(expiredOrderIds, expiredAt);
  }

  public void completePayment(CompletePaymentCommand command) {
    LocalDateTime paidAt = getCurrentTime();
    Order order = findOrder(command.orderId());
    order.completePayment(paidAt);
  }

  @Transactional
  public void completeOrder(CompleteOrderCommand command) {
    LocalDateTime completedAt = getCurrentTime();
    Order order = findOrder(command.orderId());
    order.completeOrder(completedAt);
    internalEventPort.publishOrderCompleted(order);
  }

  private Order findOrder(Long orderId) {
    return commandPort.findById(orderId);
  }

  private List<Order> findOrders(List<Long> orderIds) {
    return commandPort.findAll(orderIds);
  }

  private LocalDateTime getCurrentTime() {
    return timePort.now();
  }

  private void processCouponForPayment(PaymentCommand command, Order order) {
    if (command.couponId() != null) {
      Coupon coupon = getCoupon(command, order);
      Integer couponDiscountAmount = OrderPriceCalculator.useCoupon(order, coupon);
      order.useCoupon(couponDiscountAmount);
      internalEventPort.publishCouponUsed(order);
    }
  }

  private void createPayment(Order order) {
    clientPort.createPayment(order);
  }

  private Coupon getCoupon(PaymentCommand command, Order order) {
    return clientPort.useCoupon(
        order.getUserId(), command.couponId(), order.getOrderTimeStamp().getOrderedAt());
  }

  private List<PromotionProduct> getPromotionProducts(
      List<OrderProductAssembler> productAssemblers, LocalDateTime orderedAt) {
    return clientPort.verifyPromotion(productAssemblers, orderedAt);
  }

  private List<Product> getProducts(List<OrderProductAssembler> productAssemblers) {
    return clientPort.useStock(productAssemblers);
  }

  private void applyPromotionDiscounts(
      List<OrderProductAssembler> productAssemblers, List<PromotionProduct> promotions) {
    promotions.forEach(
        pp ->
            productAssemblers.stream()
                .filter(op -> op.getProductId().equals(pp.productId()))
                .forEach(op -> op.applyDiscountRate(pp.discountRate())));
  }

  private void applyProductPrices(
      List<OrderProductAssembler> productAssemblers, List<Product> products) {
    products.forEach(
        p ->
            productAssemblers.stream()
                .filter(op -> op.getProductId().equals(p.getProductId()))
                .forEach(op -> op.applyProductPrice(p.getProductPrice())));
  }
}
