package shop.genieus.order.infrastructure.client;

import static shop.genieus.order.global.exception.CustomNotFoundException.*;
import static shop.genieus.order.global.exception.CustomServiceUnavailableException.*;
import static shop.genieus.order.infrastructure.client.mapper.OrderClientMapper.*;

import com.genieus.common.internal.request.StockRequest;
import com.genieus.common.internal.request.UseCouponRequest;
import com.genieus.common.internal.request.VerifyPromotionRequest;
import com.genieus.common.internal.response.CouponClientResponse;
import com.genieus.common.internal.response.ProductClientResponse;
import com.genieus.common.internal.response.PromotionClientResponse;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import shop.genieus.order.application.out.client.OrderClientPort;
import shop.genieus.order.domain.model.assembler.OrderProductAssembler;
import shop.genieus.order.domain.model.vo.Coupon;
import shop.genieus.order.domain.model.vo.Product;
import shop.genieus.order.domain.model.vo.PromotionProduct;
import shop.genieus.order.infrastructure.client.feign.CouponFeignClient;
import shop.genieus.order.infrastructure.client.feign.ProductFeignClient;
import shop.genieus.order.infrastructure.client.feign.PromotionFeignClient;

@Slf4j
@Component
@RequiredArgsConstructor
public class OrderClientAdapter implements OrderClientPort {
  private final CouponFeignClient couponFeignClient;
  private final ProductFeignClient productFeignClient;
  private final PromotionFeignClient promotionFeignClient;

  @Override
  @CircuitBreaker(name = "promotionClient", fallbackMethod = "verifyPromotionFallback")
  public List<PromotionProduct> verifyPromotion(
      List<OrderProductAssembler> orderProductAssemblers, LocalDateTime orderedAt) {
    VerifyPromotionRequest request = toVerifyPromotionRequest(orderProductAssemblers, orderedAt);
    List<PromotionClientResponse> response = promotionFeignClient.verifyPromotion(request);
    validatePromotion(response);
    return toPromotionProducts(response);
  }

  @Override
  @CircuitBreaker(name = "productClient", fallbackMethod = "useStockFallback")
  public List<Product> useStock(List<OrderProductAssembler> orderProductAssemblers) {
    StockRequest request = toStockRequest(orderProductAssemblers);
    List<ProductClientResponse> response = productFeignClient.useStock(request);
    validateProduct(response);
    return toProducts(response);
  }

  @Override
  @CircuitBreaker(name = "couponClient", fallbackMethod = "useCouponFallback")
  public Coupon useCoupon(Long userId, Long couponId, LocalDateTime orderedAt) {
    UseCouponRequest request = toUseCouponRequest(userId, couponId, orderedAt);
    CouponClientResponse response = couponFeignClient.useCoupon(request);
    validateCoupon(response);
    return toCoupon(response);
  }

  private static void validatePromotion(List<PromotionClientResponse> response) {
    if (response.isEmpty()) {
      throw new PromotionNotFoundException();
    }
  }

  private static void validateProduct(List<ProductClientResponse> response) {
    if (response.isEmpty()) {
      throw new ProductNotFoundException();
    }
  }

  private static void validateCoupon(CouponClientResponse response) {
    if (response == null) {
      throw new CouponNotFoundException();
    }
  }

  public List<PromotionProduct> verifyPromotionFallback(
      List<OrderProductAssembler> orderProductAssemblers, LocalDateTime orderedAt, Throwable ex) {
    log.error("promotion-service 호출 실패 - fallback 실행: {}", ex.getMessage());
    throw new PromotionServiceFailureException();
  }

  public List<Product> useStockFallback(
      List<OrderProductAssembler> orderProductAssemblers, Throwable ex) {
    log.error("product-service 호출 실패 - fallback 실행: {}", ex.getMessage());
    throw new ProductServiceFailureException();
  }

  public Coupon useCouponFallback(
      Long userId, Long couponId, LocalDateTime orderedAt, Throwable ex) {
    log.error("coupon-service 호출 실패 - fallback 실행: {}", ex.getMessage());
    throw new CouponServiceFailureException();
  }
}
