package shop.genieus.gateway.filter.logging;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.cloud.gateway.filter.factory.rewrite.ModifyRequestBodyGatewayFilterFactory;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Slf4j
@Component
@RequiredArgsConstructor
public class RequestLoggingFilter implements GlobalFilter, Ordered {

  private final ModifyRequestBodyGatewayFilterFactory modifyRequestBodyGatewayFilterFactory;

  @Override
  public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
    GatewayFilter delegate = modifyRequestBodyGatewayFilterFactory.apply(config());
    return delegate.filter(exchange, chain);
  }

  private ModifyRequestBodyGatewayFilterFactory.Config config() {
    return new ModifyRequestBodyGatewayFilterFactory.Config()
        .setRewriteFunction(
            String.class,
            String.class,
            (exchange, body) -> {
              ServerHttpRequest request = exchange.getRequest();
              log.info(
                  "[REQUEST] ID: {}, URI: {}, Method: {}, Headers: {}, Query: {}, Body: {}",
                  request.getId(),
                  request.getURI(),
                  request.getMethod(),
                  request.getHeaders(),
                  request.getQueryParams(),
                  body);
              return Mono.justOrEmpty(body);
            });
  }

  @Override
  public int getOrder() {
    return Ordered.HIGHEST_PRECEDENCE;
  }
}
