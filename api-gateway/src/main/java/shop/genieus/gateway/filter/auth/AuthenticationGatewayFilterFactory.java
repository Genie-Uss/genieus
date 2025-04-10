package shop.genieus.gateway.filter.auth;

import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.stereotype.Component;

@Component
public class AuthenticationGatewayFilterFactory
    extends AbstractGatewayFilterFactory<AuthenticationGatewayFilterFactory.Config> {

  private final AuthenticationFilter filter;

  public AuthenticationGatewayFilterFactory(AuthenticationFilter filter) {
    super(Config.class);
    this.filter = filter;
  }

  @Override
  public GatewayFilter apply(Config config) {
    return filter;
  }

  public static class Config {}
}