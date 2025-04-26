package shop.genieus.order.infrastructure.client;

import static shop.genieus.order.global.exception.CustomNotFoundException.*;
import static shop.genieus.order.infrastructure.client.OrderClientMapper.*;

import com.genieus.common.internal.request.CreatePaymentRequest;
import com.genieus.common.internal.request.StockRequest;
import com.genieus.common.internal.request.UseCouponRequest;
import com.genieus.common.internal.request.VerifyPromotionRequest;
import com.genieus.common.internal.response.CouponClientResponse;
import com.genieus.common.internal.response.ProductClientResponse;
import com.genieus.common.internal.response.PromotionClientResponse;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import shop.genieus.order.application.out.client.OrderClientPort;
import shop.genieus.order.domain.model.assembler.OrderProductAssembler;
import shop.genieus.order.domain.model.entity.Order;
import shop.genieus.order.domain.model.vo.Coupon;
import shop.genieus.order.domain.model.vo.Product;
import shop.genieus.order.domain.model.vo.PromotionProduct;
import shop.genieus.order.infrastructure.client.coupon.CouponServiceClient;
import shop.genieus.order.infrastructure.client.payment.PaymentServiceClient;
import shop.genieus.order.infrastructure.client.product.ProductServiceClient;
import shop.genieus.order.infrastructure.client.promotion.PromotionServiceClient;

@Slf4j
@Component
@RequiredArgsConstructor
public class OrderClientAdapter implements OrderClientPort {
  private final CouponServiceClient couponServiceClient;
  private final ProductServiceClient productServiceClient;
  private final PaymentServiceClient paymentServiceClient;
  private final PromotionServiceClient promotionServiceClient;

  @Override
  public List<PromotionProduct> verifyPromotion(
      List<OrderProductAssembler> orderProductAssemblers, LocalDateTime orderedAt) {
    VerifyPromotionRequest request = toVerifyPromotionRequest(orderProductAssemblers, orderedAt);
    List<PromotionClientResponse> response = promotionServiceClient.verifyPromotion(request);
    validatePromotion(response);
    return toPromotionProducts(response);
  }

  @Override
  public List<Product> useStock(List<OrderProductAssembler> orderProductAssemblers) {
    StockRequest request = toStockRequest(orderProductAssemblers);
    List<ProductClientResponse> response = productServiceClient.useStock(request);
    validateProduct(response);
    return toProducts(response);
  }

  @Override
  public Coupon useCoupon(Long userId, Long couponId, LocalDateTime orderedAt) {
    UseCouponRequest request = toUseCouponRequest(userId, couponId, orderedAt);
    CouponClientResponse response = couponServiceClient.useCoupon(request);
    validateCoupon(response);
    return toCoupon(response);
  }

  @Override
  public void createPayment(Order order) {
    CreatePaymentRequest request = toCreatePaymentRequest(order);
    paymentServiceClient.createPayment(request);
  }

  private static void validatePromotion(List<PromotionClientResponse> response) {
    if (response == null || response.isEmpty()) {
      log.warn("프로모션 서비스의 응답이 비어있음");
      throw new PromotionNotFoundException();
    }
  }

  private static void validateProduct(List<ProductClientResponse> response) {
    if (response == null || response.isEmpty()) {
      log.warn("상품 서비스의 응답이 비어있음");
      throw new ProductNotFoundException();
    }
  }

  private static void validateCoupon(CouponClientResponse response) {
    if (response == null) {
      log.warn("쿠폰 서비스의 응답이 비어있음");
      throw new CouponNotFoundException();
    }
  }
}
