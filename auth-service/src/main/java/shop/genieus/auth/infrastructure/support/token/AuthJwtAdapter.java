package shop.genieus.auth.infrastructure.support.token;

import java.time.Instant;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import shop.genieus.auth.application.out.support.token.AuthTokenPort;
import shop.genieus.auth.domain.model.TokenPair;
import shop.genieus.auth.infrastructure.support.token.util.JwtProvider;

@Component
@RequiredArgsConstructor
public class AuthJwtAdapter implements AuthTokenPort {
  private final JwtProvider jwtProvider;

  @Override
  public TokenPair createTokenPair(String tokenIdValue, Long subject) {
    String accessTokenValue = jwtProvider.createAccessToken(subject, tokenIdValue);
    String refreshTokenValue = jwtProvider.createRefreshToken(subject, tokenIdValue);

    Instant issuedAt = Instant.now();
    return TokenPair.create(tokenIdValue, accessTokenValue, refreshTokenValue, subject, issuedAt);
  }
}
