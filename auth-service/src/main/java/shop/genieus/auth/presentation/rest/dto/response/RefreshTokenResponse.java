package shop.genieus.auth.presentation.rest.dto.response;

import shop.genieus.auth.domain.model.TokenPair;

public record RefreshTokenResponse(String accessToken, String refreshToken) {
  public static RefreshTokenResponse from(TokenPair tokenPair) {
    return new RefreshTokenResponse(
        tokenPair.getAccessTokenCredential().tokenValue(),
        tokenPair.getRefreshTokenCredential().tokenValue());
  }
}
