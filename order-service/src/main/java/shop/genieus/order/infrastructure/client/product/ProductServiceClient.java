package shop.genieus.order.infrastructure.client.product;

import com.genieus.common.internal.request.StockRequest;
import com.genieus.common.internal.response.ProductClientResponse;
import feign.FeignException.FeignClientException;
import feign.FeignException.FeignServerException;
import feign.RetryableException;
import io.github.resilience4j.circuitbreaker.CallNotPermittedException;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import shop.genieus.order.global.exception.CustomServiceUnavailableException.ProductServiceFailureException;

@Slf4j
@Component
@RequiredArgsConstructor
public class ProductServiceClient {
  private final ProductFeignClient productFeignClient;

  @CircuitBreaker(name = "productServiceClient", fallbackMethod = "useStockFallback")
  public List<ProductClientResponse> useStock(StockRequest request) {
    return productFeignClient.useStock(request);
  }

  public List<ProductClientResponse> useStockFallback(StockRequest request, Throwable ex) {
    if (ex instanceof FeignClientException) {
      log.info("상품 서비스 응답 오류: {}", ex.getMessage());
      throw (FeignClientException) ex;
    }

    if (ex instanceof FeignServerException || ex instanceof RetryableException) {
      log.warn("상품 서비스 서버 오류: {}", ex.getMessage());
      throw new ProductServiceFailureException();
    }

    if (ex instanceof CallNotPermittedException) {
      log.warn("서킷브레이커 OPEN 상태 - 상품 서비스 호출 차단됨: {}", ex.getMessage());
      throw new ProductServiceFailureException();
    }

    log.error("상품 서비스 처리 중 알 수 없는 오류 발생");
    throw new RuntimeException("상품 서비스 처리 중 알 수 없는 오류가 발생했습니다.");
  }
}
