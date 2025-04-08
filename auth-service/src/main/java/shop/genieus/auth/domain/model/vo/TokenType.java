package shop.genieus.auth.domain.model.vo;

import java.time.Duration;
import java.time.Instant;
import lombok.Getter;

@Getter
public enum TokenType {
  ACCESS(Duration.ofMinutes(15)),
  REFRESH(Duration.ofDays(7));

  private final Duration expiration;

  TokenType(Duration expiration) {
    this.expiration = expiration;
  }

  public Instant calculateExpiration(Instant issuedAt) {
    return issuedAt.plus(expiration);
  }
}
