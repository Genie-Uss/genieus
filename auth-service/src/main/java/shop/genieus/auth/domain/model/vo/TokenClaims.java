package shop.genieus.auth.domain.model.vo;

import java.time.Instant;

public record TokenClaims(Long subject, Instant issuedAt, Instant expiration) {

  public TokenClaims {
    if (subject == null) {
      throw new IllegalArgumentException("발급자는 필수입니다.");
    }
    validateInstants(issuedAt, expiration);
  }

  public static TokenClaims of(Long subject, Instant issuedAt, Instant expiration) {
    return new TokenClaims(subject, issuedAt, expiration);
  }

  private static void validateInstants(Instant issuedAt, Instant expiration) {
    if (issuedAt == null) {
      throw new IllegalArgumentException("세션 정보가 필요합니다.");
    }
    if (expiration == null) {
      throw new IllegalArgumentException("만료일(expiration)은 필수입니다.");
    }
    if (expiration.isBefore(issuedAt)) {
      throw new IllegalArgumentException("만료일은 발급일보다 이후여야 합니다.");
    }
  }
}
