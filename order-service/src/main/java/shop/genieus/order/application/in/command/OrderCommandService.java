package shop.genieus.order.application.in.command;

import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import shop.genieus.order.application.in.command.dto.*;
import shop.genieus.order.application.in.event.OrderEventPublisher;
import shop.genieus.order.application.out.client.OrderClientPort;
import shop.genieus.order.application.out.persistence.OrderCommandPort;
import shop.genieus.order.application.out.util.OrderTimePort;
import shop.genieus.order.application.policy.OrderCancelPolicy;
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
@Transactional
@RequiredArgsConstructor
public class OrderCommandService {
  private final OrderTimePort orderTimePort;
  private final OrderClientPort orderClientPort;
  private final OrderCommandPort orderCommandPort;
  private final OrderCancelPolicy orderCancelPolicy;
  private final OrderEventPublisher orderEventPublisher;

  /**
   * 주문 생성 명령을 처리하여 새로운 주문을 생성하고 저장한 후, 주문 생성 이벤트를 발행합니다.
   *
   * @param command 주문 생성에 필요한 정보가 담긴 명령 객체
   * @return 생성 및 저장된 주문 객체
   */
  public Order create(CreateOrderCommand command) {
    LocalDateTime orderedAt = getCurrentTime();
    CreateOrderAssembler assembler = command.toAssembler();
    assembler.applyOrderedAt(orderedAt);

    List<OrderProductAssembler> productAssemblers = command.toProductAssembler();
    List<PromotionProduct> promotions = getPromotionProducts(productAssemblers, orderedAt);
    List<Product> products = getProducts(productAssemblers);

    applyPromotionDiscounts(productAssemblers, promotions);
    applyProductPrices(productAssemblers, products);

    List<OrderProduct> orderProducts =
        productAssemblers.stream().map(OrderProduct::create).toList();
    assembler.applyOrderProducts(orderProducts);

    OrderPriceCalculator.calculate(assembler);

    Order order = Order.create(assembler);
    Order saved = orderCommandPort.save(order);

    orderEventPublisher.publishOrderCreated(order);
    return saved;
  }

  /**
   * 주문에 대한 결제 요청을 처리하고 결제 프로세스를 시작합니다.
   *
   * 결제 요청 시 쿠폰이 포함되어 있으면 쿠폰을 적용하며, 주문 상태를 결제 요청으로 변경한 후 결제 생성을 외부 시스템에 위임합니다.
   *
   * @param command 결제 요청에 필요한 정보가 담긴 명령 객체
   * @return 결제 요청이 반영된 주문 객체
   */
  public Order requestPayment(PaymentCommand command) {
    LocalDateTime paymentRequested = getCurrentTime();
    Order order = findOrder(command.orderId());
    processCouponForPayment(command, order);
    order.requestPayment(paymentRequested);
    createPayment(order);
    return order;
  }

  /**
   * 사용자가 주문을 취소하도록 처리하고, 주문 취소 이벤트를 발행합니다.
   *
   * @param command 주문 취소에 필요한 정보(주문 ID, 사용자 ID 등)
   */
  public void cancelOrderByUser(CancelOrderCommand command) {
    LocalDateTime canceledAt = getCurrentTime();
    Order order = findOrder(command.orderId());
    orderCancelPolicy.cancelOrderByUser(order, command.userId(), canceledAt);
    orderEventPublisher.publishOrderCanceled(order);
  }

  /**
   * 시스템에 의해 주문을 취소하고 주문 취소 이벤트를 발행합니다.
   *
   * @param command 주문 취소에 필요한 정보를 담은 커맨드 객체
   */
  public void cancelOrderBySystem(CancelOrderCommand command) {
    LocalDateTime canceledAt = getCurrentTime();
    Order order = findOrder(command.orderId());
    orderCancelPolicy.cancelOrderBySystem(order, canceledAt);
    orderEventPublisher.publishOrderCanceled(order);
  }

  /**
   * 결제 완료 명령을 처리하여 주문의 결제 상태를 완료로 변경하고, 주문 완료 이벤트를 발행합니다.
   *
   * @param command 결제 완료에 필요한 정보를 담은 명령 객체
   */
  public void completePayment(CompletePaymentCommand command) {
    LocalDateTime paidAt = getCurrentTime();
    Order order = findOrder(command.orderId());
    order.completePayment(paidAt);
    orderEventPublisher.publishOrderCompleted(order);
  }

  /**
   * 주문을 완료 상태로 변경합니다.
   *
   * @param command 주문 완료 명령 객체
   */
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

  private void createPayment(Order order) {
    orderClientPort.createPayment(order);
  }

  private Coupon getCoupon(PaymentCommand command, Order order) {
    return orderClientPort.useCoupon(order.getUserId(), command.couponId(), order.getOrderedAt());
  }

  private List<PromotionProduct> getPromotionProducts(
      List<OrderProductAssembler> productAssemblers, LocalDateTime orderedAt) {
    return orderClientPort.verifyPromotion(productAssemblers, orderedAt);
  }

  private List<Product> getProducts(List<OrderProductAssembler> productAssemblers) {
    return orderClientPort.useStock(productAssemblers);
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
