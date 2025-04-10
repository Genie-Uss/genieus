package shop.genieus.gateway.filter.logging;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.cloud.gateway.filter.factory.rewrite.ModifyResponseBodyGatewayFilterFactory;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Slf4j
@Component
@RequiredArgsConstructor
public class ResponseLoggingFilter implements GlobalFilter, Ordered {

  private final ModifyResponseBodyGatewayFilterFactory modifyResponseBodyGatewayFilterFactory;

  @Override
  public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
    GatewayFilter delegate = modifyResponseBodyGatewayFilterFactory.apply(config());
    return delegate.filter(exchange, chain);
  }

  private ModifyResponseBodyGatewayFilterFactory.Config config() {
    return new ModifyResponseBodyGatewayFilterFactory.Config()
        .setRewriteFunction(
            String.class,
            String.class,
            (exchange, body) -> {
              ServerHttpRequest request = exchange.getRequest();
              ServerHttpResponse response = exchange.getResponse();

              log.info(
                  "[RESPONSE] ID: {}, URI: {}, Status: {}, Headers: {}, Body: {}",
                  request.getId(),
                  request.getURI(),
                  response.getStatusCode(),
                  response.getHeaders(),
                  body);
              return Mono.justOrEmpty(body);
            });
  }

  @Override
  public int getOrder() {
    return Ordered.HIGHEST_PRECEDENCE + 1;
  }
}
