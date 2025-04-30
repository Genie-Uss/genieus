package shop.genieus.order.infrastructure.client.payment;

import com.genieus.common.internal.request.CreatePaymentRequest;
import feign.FeignException.FeignClientException;
import feign.FeignException.FeignServerException;
import feign.RetryableException;
import io.github.resilience4j.circuitbreaker.CallNotPermittedException;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import shop.genieus.order.global.exception.CustomServiceUnavailableException.PaymentServiceFailureException;

@Slf4j
@Component
@RequiredArgsConstructor
public class PaymentServiceClient {
  private final PaymentFeignClient paymentFeignClient;

  @Retry(name = "paymentServiceClient")
  @CircuitBreaker(name = "paymentServiceClient", fallbackMethod = "createPaymentFallback")
  public void createPayment(CreatePaymentRequest request) {
    paymentFeignClient.createPayment(request);
  }

  public void createPaymentFallback(CreatePaymentRequest request, Throwable ex) {
    if (ex instanceof FeignClientException) {
      log.info("결제 서비스 응답 오류: {}", ex.getMessage());
      throw (FeignClientException) ex;
    }

    if (ex instanceof FeignServerException || ex instanceof RetryableException) {
      log.warn("결제 서비스 서버 오류: {}", ex.getMessage());
      throw new PaymentServiceFailureException();
    }

    if (ex instanceof CallNotPermittedException) {
      log.warn("서킷브레이커 OPEN 상태 - 결제 서비스 호출 차단됨: {}", ex.getMessage());
      throw new PaymentServiceFailureException();
    }

    log.error("결제 서비스 처리 중 알 수 없는 오류 발생");
    throw new RuntimeException("결제 서비스 처리 중 알 수 없는 오류가 발생했습니다.");
  }
}
