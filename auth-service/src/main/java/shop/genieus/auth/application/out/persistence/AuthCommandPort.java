package shop.genieus.auth.application.out.persistence;

import java.time.Instant;
import shop.genieus.auth.domain.model.Passport;
import shop.genieus.auth.domain.model.entity.User;
import shop.genieus.auth.domain.model.vo.TokenCredential;
import shop.genieus.auth.domain.model.vo.TokenId;

public interface AuthCommandPort {
  User findByEmail(String username);

  void saveRefreshToken(TokenId tokenId, Long userId, TokenCredential refreshTokenCredential);

  void addToBlacklist(TokenId tokenId, Instant expirationTime);

  void removeRefreshToken(TokenId tokenId, Long userId);

  boolean isBlacklisted(TokenId tokenId);

  boolean isValidRefreshToken(TokenId tokenId, String refreshToken);

  User findByUserId(Long userId);

  Passport findPassportFromCache(Long userId);

  Passport savePassport(Passport passport);

  boolean existsByEmail(String email);

  User save(User user);
}
