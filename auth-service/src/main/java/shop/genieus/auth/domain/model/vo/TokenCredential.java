package shop.genieus.auth.domain.model.vo;

import java.time.Instant;

public record TokenCredential(String tokenValue, TokenType tokenType, TokenClaims claims) {

  public TokenCredential {
    validateTokenValue(tokenValue);
    validateTokenType(tokenType);
  }

  public static TokenCredential accessTokenOf(
      String tokenValue, Long subject, Instant issuedAt, Instant expiration) {
    TokenClaims claims = TokenClaims.of(subject, issuedAt, expiration);
    return new TokenCredential(tokenValue, TokenType.ACCESS, claims);
  }

  public static TokenCredential refreshTokenOf(
      String tokenValue, Long subject, Instant issuedAt, Instant expiration) {
    TokenClaims claims = TokenClaims.of(subject, issuedAt, expiration);
    return new TokenCredential(tokenValue, TokenType.REFRESH, claims);
  }

  private static void validateTokenValue(String tokenValue) {
    if (tokenValue == null || tokenValue.isBlank()) {
      throw new IllegalArgumentException("토큰 값은 빈 값일 수 없습니다.");
    }
  }

  private static void validateTokenType(TokenType tokenType) {
    if (tokenType == null) {
      throw new IllegalArgumentException("유효하지 않은 토큰 타입입니다.");
    }
  }

  public boolean isAccessToken() {
    return this.tokenType == TokenType.ACCESS;
  }

  public boolean isRefreshToken() {
    return this.tokenType == TokenType.REFRESH;
  }
}
