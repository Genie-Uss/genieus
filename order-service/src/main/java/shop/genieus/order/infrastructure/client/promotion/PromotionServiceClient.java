package shop.genieus.order.infrastructure.client.promotion;

import com.genieus.common.internal.request.VerifyPromotionRequest;
import com.genieus.common.internal.response.PromotionClientResponse;
import feign.FeignException.FeignClientException;
import feign.FeignException.FeignServerException;
import feign.RetryableException;
import io.github.resilience4j.circuitbreaker.CallNotPermittedException;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import shop.genieus.order.domain.model.vo.Promotion;
import shop.genieus.order.global.exception.CustomServiceUnavailableException.PromotionServiceFailureException;

@Slf4j
@Component
@RequiredArgsConstructor
public class PromotionServiceClient {
  private final PromotionFeignClient promotionFeignClient;

  @CircuitBreaker(name = "promotionServiceClient", fallbackMethod = "verifyPromotionFallback")
  public List<PromotionClientResponse> verifyPromotion(VerifyPromotionRequest request) {
    return promotionFeignClient.verifyPromotion(request);
  }

  public List<Promotion> verifyPromotionFallback(VerifyPromotionRequest request, Throwable ex) {

    if (ex instanceof FeignClientException) {
      log.info("프로모션 서비스 응답 오류: {}", ex.getMessage());
      throw (FeignClientException) ex;
    }

    if (ex instanceof FeignServerException || ex instanceof RetryableException) {
      log.warn("프로모션 서비스 서버 오류: {}", ex.getMessage());
      throw new PromotionServiceFailureException();
    }

    if (ex instanceof CallNotPermittedException) {
      log.warn("서킷브레이커 OPEN 상태 - 프로모션 서비스 호출 차단됨: {}", ex.getMessage());
      throw new PromotionServiceFailureException();
    }

    log.error("프로모션 서비스 처리 중 알 수 없는 오류 발생");
    throw new RuntimeException("서비스 처리 중 알 수 없는 오류가 발생했습니다.");
  }
}
