package shop.genieus.gateway.config;

import static shop.genieus.gateway.constants.AppConstants.Trace.TRACE_ID;

import brave.Tracer;
import lombok.RequiredArgsConstructor;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
@RequiredArgsConstructor
public class WebClientConfig {

  private final Tracer tracer;

  @Bean
  @LoadBalanced
  public WebClient.Builder webClient() {
    return WebClient.builder()
        .filter(
            (request, next) -> {
              brave.Span currentSpan = tracer.currentSpan();
              if (currentSpan != null) {
                return next.exchange(request)
                    .contextWrite(ctx -> ctx.put(TRACE_ID, currentSpan.context().traceIdString()));
              }
              return next.exchange(request);
            });
  }
}
