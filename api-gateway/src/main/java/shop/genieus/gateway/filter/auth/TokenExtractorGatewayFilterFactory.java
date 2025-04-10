package shop.genieus.gateway.filter.auth;

import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.stereotype.Component;

@Component
public class TokenExtractorGatewayFilterFactory
    extends AbstractGatewayFilterFactory<TokenExtractorGatewayFilterFactory.Config> {

  private final TokenExtractorFilter filter;

  public TokenExtractorGatewayFilterFactory(TokenExtractorFilter filter) {
    super(Config.class);
    this.filter = filter;
  }

  @Override
  public GatewayFilter apply(Config config) {
    return filter;
  }

  public static class Config {}
}
