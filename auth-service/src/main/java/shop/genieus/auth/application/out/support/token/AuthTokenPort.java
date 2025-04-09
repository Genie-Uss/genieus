package shop.genieus.auth.application.out.support.token;

import shop.genieus.auth.domain.model.TokenPair;

public interface AuthTokenPort {
  TokenPair createTokenPair(String tokenId, Long userId);
}
