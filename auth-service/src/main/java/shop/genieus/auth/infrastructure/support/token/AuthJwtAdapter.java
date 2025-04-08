package shop.genieus.auth.infrastructure.support.token;

import io.jsonwebtoken.Claims;
import java.time.Instant;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import shop.genieus.auth.application.out.support.token.AuthTokenPort;
import shop.genieus.auth.domain.model.TokenPair;
import shop.genieus.auth.domain.model.TokenValidationResult;
import shop.genieus.auth.infrastructure.support.token.util.JwtProvider;
import shop.genieus.auth.infrastructure.support.token.util.JwtValidator;

@Component
@RequiredArgsConstructor
public class AuthJwtAdapter implements AuthTokenPort {
  private final JwtProvider jwtProvider;
  private final JwtValidator jwtValidator;

  @Override
  public TokenPair createTokenPair(String tokenIdValue, Long subject) {
    String accessTokenValue = jwtProvider.createAccessToken(subject, tokenIdValue);
    String refreshTokenValue = jwtProvider.createRefreshToken(subject, tokenIdValue);

    Instant issuedAt = Instant.now();
    return TokenPair.create(tokenIdValue, accessTokenValue, refreshTokenValue, subject, issuedAt);
  }

  @Override
  public Instant getExpirationTime(String accessToken) {
    return jwtValidator.getExpirationTime(accessToken);
  }

  @Override
  public TokenValidationResult validateTokenAndExtractId(String token) {
    Claims claims = jwtValidator.validateToken(token);

    return TokenValidationResult.create(claims.getId(), Long.parseLong(claims.getSubject()));
  }
}
