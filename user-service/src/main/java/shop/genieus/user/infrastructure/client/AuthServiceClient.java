package shop.genieus.user.infrastructure.client;

import com.genieus.common.internal.request.AuthUserClientRequest;
import feign.FeignException.FeignClientException;
import feign.FeignException.FeignServerException;
import feign.RetryableException;
import io.github.resilience4j.circuitbreaker.CallNotPermittedException;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import shop.genieus.user.global.exception.AuthServiceFailureException;

@Slf4j
@Component
@RequiredArgsConstructor
public class AuthServiceClient {
  private final AuthFeignClient feignClient;

  @CircuitBreaker(name = "authServiceClient", fallbackMethod = "registerUserFallback")
  public void registerAuthUser(AuthUserClientRequest request) {
    feignClient.registerAuthUser(request);
    log.info("auth service에 인증 유저 등록 완료, 유저 이메일: {}", request.email());
  }

  public void registerUserFallback(AuthUserClientRequest request, Throwable ex) {
    if (ex instanceof FeignClientException) {
      log.error("인증 서비스 응답 오류: {}", ex.getMessage());
      throw (FeignClientException) ex;
    }

    if (ex instanceof FeignServerException || ex instanceof RetryableException) {
      log.error("인증 서비스 서버 오류: {}", ex.getMessage());
      throw new AuthServiceFailureException();
    }

    if (ex instanceof CallNotPermittedException) {
      log.error("서킷브레이커 OPEN 상태 - 인증 서비스 호출 차단됨: {}", ex.getMessage());
      throw new AuthServiceFailureException();
    }

    log.error("인증 서비스 처리 중 알 수 없는 오류 발생");
    throw new RuntimeException("인증 서비스 처리 중 알 수 없는 오류가 발생했습니다.");
  }
}
