package shop.genieus.auth.domain.model;

import java.time.Instant;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import shop.genieus.auth.domain.model.vo.TokenCredential;
import shop.genieus.auth.domain.model.vo.TokenId;
import shop.genieus.auth.domain.model.vo.TokenType;

@Getter
@Builder(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class TokenPair {
  private final TokenId tokenId;
  private TokenCredential accessTokenCredential;
  private TokenCredential refreshTokenCredential;

  public static TokenPair create(
      String tokenIdValue,
      String accessTokenValue,
      String refreshTokenValue,
      Long subject,
      Instant issuedAt) {
    validateCreateParams(tokenIdValue, accessTokenValue, refreshTokenValue, subject, issuedAt);

    Instant accessTokenExpiration = TokenType.ACCESS.calculateExpiration(issuedAt);
    Instant refreshTokenExpiration = TokenType.REFRESH.calculateExpiration(issuedAt);

    return TokenPair.create(
        tokenIdValue,
        accessTokenValue,
        refreshTokenValue,
        subject,
        issuedAt,
        accessTokenExpiration,
        issuedAt,
        refreshTokenExpiration);
  }

  private static TokenPair create(
      String tokenIdValue,
      String accessToken,
      String refreshToken,
      Long subject,
      Instant accessTokenIssuedAt,
      Instant accessTokenExpiration,
      Instant refreshTokenIssuedAt,
      Instant refreshTokenExpiration) {
    TokenId tokenId = TokenId.of(tokenIdValue);

    TokenCredential accessTokenCredential =
        createAccessTokenCredential(
            accessToken, subject, accessTokenIssuedAt, accessTokenExpiration);

    TokenCredential refreshTokenCredential =
        createRefreshTokenCredential(
            refreshToken, subject, refreshTokenIssuedAt, refreshTokenExpiration);

    validateTokenPair(accessTokenCredential, refreshTokenCredential);

    return TokenPair.builder()
        .tokenId(tokenId)
        .accessTokenCredential(accessTokenCredential)
        .refreshTokenCredential(refreshTokenCredential)
        .build();
  }

  private static TokenCredential createAccessTokenCredential(
      String token, Long subject, Instant issuedAt, Instant expiration) {
    return TokenCredential.accessTokenOf(token, subject, issuedAt, expiration);
  }

  private static TokenCredential createRefreshTokenCredential(
      String token, Long subject, Instant issuedAt, Instant expiration) {
    return TokenCredential.refreshTokenOf(token, subject, issuedAt, expiration);
  }

  private static void validateTokenPair(
      TokenCredential accessTokenCredential, TokenCredential refreshTokenCredential) {
    if (!(accessTokenCredential.isAccessToken() && refreshTokenCredential.isRefreshToken())) {
      throw new IllegalArgumentException("유효하지 않은 토큰 타입입니다.");
    }

    Long accessSubject = accessTokenCredential.claims().subject();
    Long refreshSubject = refreshTokenCredential.claims().subject();

    if (!accessSubject.equals(refreshSubject)) {
      throw new IllegalArgumentException("액세스 토큰과 리프레시 토큰이 일치하지 않습니다.");
    }
  }

  private static void validateCreateParams(
      String tokenIdValue,
      String accessTokenValue,
      String refreshTokenValue,
      Long subject,
      Instant issuedAt) {
    if (tokenIdValue == null || tokenIdValue.isBlank()) {
      throw new IllegalArgumentException("토큰 ID는 빈 값일 수 없습니다.");
    }
    if (accessTokenValue == null || accessTokenValue.isBlank()) {
      throw new IllegalArgumentException("액세스 토큰 값은 빈 값일 수 없습니다.");
    }
    if (refreshTokenValue == null || refreshTokenValue.isBlank()) {
      throw new IllegalArgumentException("리프레시 토큰 값은 빈 값일 수 없습니다.");
    }
    if (subject == null || subject <= 0) {
      throw new IllegalArgumentException("유효하지 않은 사용자 ID입니다.");
    }
    if (issuedAt == null) {
      throw new IllegalArgumentException("발급 시간은 필수입니다.");
    }
  }
}
