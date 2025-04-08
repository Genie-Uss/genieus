package shop.genieus.auth.infrastructure.support.token.util;

import java.security.Key;
import lombok.RequiredArgsConstructor;
import shop.genieus.auth.global.config.JwtConfig;

@RequiredArgsConstructor
public abstract class AbstractJwtSupport {
  protected final Key key;

  public AbstractJwtSupport(JwtConfig jwtConfig) {
    this.key = jwtConfig.getKey();
  }
}
