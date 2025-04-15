package shop.genieus.gateway.config;

import static shop.genieus.gateway.constants.AppConstants.Trace.TRACE_ID;

import brave.Tracer;
import lombok.RequiredArgsConstructor;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.ClientRequest;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
@RequiredArgsConstructor
public class WebClientConfig {

  private final Tracer tracer;

  /**
   * 로드 밸런싱과 트레이싱이 적용된 WebClient.Builder 빈을 생성합니다.
   *
   * 현재 Brave 트레이싱 스팬이 존재할 경우, 해당 스팬의 traceId와 spanId를 조합한 "b3" 헤더를 모든 HTTP 요청에 추가합니다.
   * 또한 traceId를 리액티브 컨텍스트에 저장하여 추적 정보를 전파합니다.
   *
   * @return 트레이싱 및 로드 밸런싱이 적용된 WebClient.Builder 인스턴스
   */
  @Bean
  @LoadBalanced
  public WebClient.Builder webClient() {
    return WebClient.builder()
        .filter(
            (request, next) -> {
              brave.Span currentSpan = tracer.currentSpan();
              if (currentSpan != null) {
                String b3 =
                    String.format(
                        "%s-%s-1",
                        currentSpan.context().traceIdString(),
                        currentSpan.context().spanIdString());
                request = ClientRequest.from(request).header("b3", b3).build();

                return next.exchange(request)
                    .contextWrite(ctx -> ctx.put(TRACE_ID, currentSpan.context().traceIdString()));
              }
              return next.exchange(request);
            });
  }
}
