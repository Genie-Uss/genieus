package shop.genieus.auth.presentation.rest.dto.response;

import shop.genieus.auth.domain.model.TokenPair;

public record LoginResponse(String accessToken, String refreshToken) {
  public static LoginResponse from(TokenPair tokenPair) {
    return new LoginResponse(
        tokenPair.getAccessTokenCredential().tokenValue(),
        tokenPair.getRefreshTokenCredential().tokenValue());
  }
}
