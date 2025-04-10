package shop.genieus.gateway.config;

import static shop.genieus.gateway.constants.AppConstants.Trace.SPAN_ID;
import static shop.genieus.gateway.constants.AppConstants.Trace.TRACE_ID;

import brave.Tracer;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.server.WebFilter;
import reactor.core.publisher.Hooks;

@Configuration
@RequiredArgsConstructor
public class WebFilterConfig {

  private final Tracer tracer;

  @Bean
  public WebFilter tracingWebFilter() {
    Hooks.enableAutomaticContextPropagation();

    return (exchange, chain) -> {
      brave.Span currentSpan = tracer.currentSpan();
      if (currentSpan != null) {
        String traceId = currentSpan.context().traceIdString();
        String spanId = currentSpan.context().spanIdString();

        return chain
            .filter(exchange)
            .contextWrite(context -> context.put(TRACE_ID, traceId).put(SPAN_ID, spanId));
      }
      return chain.filter(exchange);
    };
  }
}
