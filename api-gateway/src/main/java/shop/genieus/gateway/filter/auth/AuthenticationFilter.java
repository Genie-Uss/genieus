package shop.genieus.gateway.filter.auth;

import static shop.genieus.gateway.constants.AppConstants.Filter.ERROR_MISSING_TOKEN;
import static shop.genieus.gateway.constants.AppConstants.Filter.TOKEN_ATTR;

import java.nio.charset.StandardCharsets;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import shop.genieus.gateway.client.AuthServiceClient;

@Slf4j
@RequiredArgsConstructor
public class AuthenticationFilter implements GatewayFilter {
  private static final String AUTH_ERROR = "인증 서비스 오류가 발생했습니다";

  private final AuthServiceClient authServiceClient;

  @Override
  public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
    ServerHttpRequest request = exchange.getRequest();
    log.debug("Authorization Filter 실행: {}", request.getPath());

    String token = (String) exchange.getAttributes().get(TOKEN_ATTR);
    if (token == null) {
      return handleForbidden(exchange, ERROR_MISSING_TOKEN);
    }

    return authServiceClient
        .authorize(token, request.getPath().value(), request.getMethod().name())
        .flatMap(
            response -> {
              String encodedPassport = response.encodedPassport();
              String passportHeaderKey = response.passportHeaderKey();
              if (encodedPassport != null && passportHeaderKey != null) {
                ServerHttpRequest mutatedRequest =
                    exchange
                        .getRequest()
                        .mutate()
                        .header(passportHeaderKey, encodedPassport)
                        .build();
                return chain.filter(exchange.mutate().request(mutatedRequest).build());
              } else {
                return chain.filter(exchange);
              }
            })
        .onErrorResume(
            e -> {
              log.error("인증 서비스 오류: {}", e.getMessage(), e);

              if (e instanceof WebClientResponseException) {
                WebClientResponseException wcre = (WebClientResponseException) e;
                log.error(
                    "응답 상태 코드: {}, 응답 본문: {}",
                    wcre.getStatusCode(),
                    wcre.getResponseBodyAsString());

                return handleErrorResponse(exchange, wcre);
              }

              return handleForbidden(exchange, AUTH_ERROR);
            });
  }

  private Mono<Void> handleForbidden(ServerWebExchange exchange, String message) {
    ServerHttpResponse response = exchange.getResponse();
    response.setStatusCode(HttpStatus.FORBIDDEN);

    DataBuffer buffer = response.bufferFactory().wrap(message.getBytes(StandardCharsets.UTF_8));
    return response.writeWith(Mono.just(buffer));
  }

  private Mono<Void> handleErrorResponse(
      ServerWebExchange exchange, WebClientResponseException wcre) {
    ServerHttpResponse response = exchange.getResponse();

    response.setStatusCode(wcre.getStatusCode());

    if (wcre.getHeaders().getContentType() != null) {
      response.getHeaders().setContentType(wcre.getHeaders().getContentType());
    } else {
      response.getHeaders().setContentType(MediaType.APPLICATION_JSON);
    }

    DataBuffer buffer = response.bufferFactory().wrap(wcre.getResponseBodyAsByteArray());
    return response.writeWith(Mono.just(buffer));
  }
}
