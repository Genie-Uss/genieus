package shop.genieus.gateway.config;

import static shop.genieus.gateway.constants.AppConstants.Trace.TRACE_ID;

import brave.Tracer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class GlobalFilterConfig {

  private final Tracer tracer;

  @Bean
  public GlobalFilter traceIdFilter() {
    return (exchange, chain) -> {
      brave.Span currentSpan = tracer.currentSpan();

      if (currentSpan != null) {
        String traceId = currentSpan.context().traceIdString();
        return chain.filter(exchange).contextWrite(ctx -> ctx.put(TRACE_ID, traceId));
      }

      return chain.filter(exchange);
    };
  }
}
