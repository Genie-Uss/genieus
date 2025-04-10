package shop.genieus.gateway.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import shop.genieus.gateway.client.AuthServiceClient;
import shop.genieus.gateway.filter.auth.AuthenticationFilter;
import shop.genieus.gateway.filter.auth.AuthenticationGatewayFilterFactory;
import shop.genieus.gateway.filter.auth.TokenExtractorFilter;
import shop.genieus.gateway.filter.auth.TokenExtractorGatewayFilterFactory;

@Configuration
@RequiredArgsConstructor
public class GatewayConfig {

  private final AuthServiceClient authServiceClient;

  @Bean
  public TokenExtractorFilter tokenExtractorFilter() {
    return new TokenExtractorFilter();
  }

  @Bean
  public AuthenticationFilter authenticationFilter() {
    return new AuthenticationFilter(authServiceClient);
  }

  @Bean
  public TokenExtractorGatewayFilterFactory tokenExtractorGatewayFilterFactory() {
    return new TokenExtractorGatewayFilterFactory(tokenExtractorFilter());
  }

  @Bean
  public AuthenticationGatewayFilterFactory authenticationGatewayFilterFactory() {
    return new AuthenticationGatewayFilterFactory(authenticationFilter());
  }
}
