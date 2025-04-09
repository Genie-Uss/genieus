package shop.genieus.auth.application.out.support.token;

import java.time.Instant;
import shop.genieus.auth.domain.model.TokenPair;
import shop.genieus.auth.domain.model.TokenValidationResult;

public interface AuthTokenPort {
  TokenPair createTokenPair(String tokenId, Long userId);

  Instant getExpirationTime(String accessToken);

  TokenValidationResult validateTokenAndExtractId(String token);
}
