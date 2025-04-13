package shop.genieus.order.infrastructure.client.coupon;

import com.genieus.common.internal.request.UseCouponRequest;
import com.genieus.common.internal.response.CouponClientResponse;
import feign.FeignException.FeignClientException;
import feign.FeignException.FeignServerException;
import feign.RetryableException;
import io.github.resilience4j.circuitbreaker.CallNotPermittedException;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import shop.genieus.order.global.exception.CustomServiceUnavailableException.CouponServiceFailureException;

@Slf4j
@Component
@RequiredArgsConstructor
public class CouponServiceClient {
  private final CouponFeignClient couponFeignClient;

  @CircuitBreaker(name = "couponServiceClient", fallbackMethod = "useCouponFallback")
  public CouponClientResponse useCoupon(UseCouponRequest request) {
    return couponFeignClient.useCoupon(request);
  }

  public CouponClientResponse useCouponFallback(UseCouponRequest request, Throwable ex) {
    if (ex instanceof FeignClientException) {
      log.error("쿠폰 서비스 응답 오류: {}", ex.getMessage());
      throw (FeignClientException) ex;
    }

    if (ex instanceof FeignServerException || ex instanceof RetryableException) {
      log.error("쿠폰 서비스 서버 오류: {}", ex.getMessage());
      throw new CouponServiceFailureException();
    }

    if (ex instanceof CallNotPermittedException) {
      log.error("서킷브레이커 OPEN 상태 - 쿠폰 서비스 호출 차단됨: {}", ex.getMessage());
      throw new CouponServiceFailureException();
    }

    log.error("쿠폰 서비스 처리 중 알 수 없는 오류 발생");
    throw new RuntimeException("쿠폰 서비스 처리 중 알 수 없는 오류가 발생했습니다.");
  }
}
