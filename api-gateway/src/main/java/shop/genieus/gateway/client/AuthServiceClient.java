package shop.genieus.gateway.client;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;
import shop.genieus.gateway.client.dto.AuthClientRequest;
import shop.genieus.gateway.client.dto.AuthClientResponse;
import shop.genieus.gateway.constants.AppConstants.ApiPath.Internal;

@Slf4j
@Component
public class AuthServiceClient {
  private final WebClient.Builder webClientBuilder;
  @Value("${web.client.url.auth}")
  private String AUTH_SERVICE_URL;

  public AuthServiceClient(WebClient.Builder webClientBuilder) {
    this.webClientBuilder = webClientBuilder;
  }

  public Mono<AuthClientResponse> authorize(String token, String uri, String method) {
    log.debug("인가 요청: uri={}, method={}", uri, method);

    return webClientBuilder
        .build()
        .post()
        .uri(AUTH_SERVICE_URL + Internal.AUTH_ISSUE_PASSPORT)
        .contentType(MediaType.APPLICATION_JSON)
        .bodyValue(new AuthClientRequest(token, uri, method))
        .retrieve()
        .bodyToMono(AuthClientResponse.class)
        .doOnNext(response -> log.debug("인가 응답: {}", response.toString()))
        .doOnError(
            e -> {
              log.error("인가 처리 오류: {}", e.getMessage());
              if (e instanceof WebClientResponseException) {
                WebClientResponseException wcre = (WebClientResponseException) e;
                log.error(
                    "응답 상태 코드: {}, 응답 본문: {}",
                    wcre.getStatusCode(),
                    wcre.getResponseBodyAsString());
              }
            });
  }
}
