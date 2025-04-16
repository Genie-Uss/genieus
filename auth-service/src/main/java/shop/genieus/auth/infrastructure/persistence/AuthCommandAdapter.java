package shop.genieus.auth.infrastructure.persistence;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.time.Instant;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import shop.genieus.auth.application.out.persistence.AuthCommandPort;
import shop.genieus.auth.domain.model.Passport;
import shop.genieus.auth.domain.model.entity.User;
import shop.genieus.auth.domain.model.vo.Email;
import shop.genieus.auth.domain.model.vo.TokenCredential;
import shop.genieus.auth.domain.model.vo.TokenId;
import shop.genieus.auth.infrastructure.persistence.repository.PassportRedisRepository;
import shop.genieus.auth.infrastructure.persistence.repository.TokenRedisRepository;
import shop.genieus.auth.infrastructure.persistence.repository.UserJpaRepository;

@Slf4j
@Component
@RequiredArgsConstructor
public class AuthCommandAdapter implements AuthCommandPort {
  private final UserJpaRepository userJpaRepository;
  private final TokenRedisRepository tokenRedisRepository;
  private final PassportRedisRepository passportRedisRepository;

  @PersistenceContext private EntityManager entityManager;

  @Override
  public User findByEmail(String username) {
    return userJpaRepository
        .findByEmailNotDeleted(Email.of(username))
        .orElseThrow(() -> new IllegalArgumentException("입력하신 아이디 또는 비밀번호가 잘못되었습니다."));
  }

  @Override
  public void saveRefreshToken(
      TokenId tokenId, Long userId, TokenCredential refreshTokenCredential) {
    long ttlMillis = refreshTokenCredential.tokenType().getExpiration().toMillis();
    String tokenValue = tokenId.value();
    tokenRedisRepository.saveRefreshToken(
        tokenValue, userId, refreshTokenCredential.tokenValue(), ttlMillis);
    log.info("리프레시 토큰 저장: userId={}, tokenId={}", userId, tokenValue);
  }

  @Override
  public void addToBlacklist(TokenId tokenId, Instant expiration) {
    long ttlMillis = java.time.temporal.ChronoUnit.MILLIS.between(Instant.now(), expiration);
    if (ttlMillis <= 0) {
      return;
    }
    ttlMillis += 60_000;
    tokenRedisRepository.addToBlacklist(tokenId.value(), ttlMillis);
  }

  @Override
  public void removeRefreshToken(TokenId tokenId, Long userId) {
    tokenRedisRepository.removeRefreshToken(tokenId.value(), userId);
  }

  @Override
  public boolean isBlacklisted(TokenId tokenId) {
    return tokenRedisRepository.isBlacklisted(tokenId.value());
  }

  @Override
  public boolean isValidRefreshToken(TokenId userId, String refreshToken) {
    if (userId == null || refreshToken == null || refreshToken.isEmpty()) {
      return false;
    }
    String storedToken = tokenRedisRepository.getRefreshToken(userId.value());

    return storedToken != null && storedToken.equals(refreshToken);
  }

  @Override
  public User findByUserId(Long userId) {
    return userJpaRepository
        .findByIdNotDeleted(userId)
        .orElseThrow(() -> new IllegalArgumentException("회원이 존재하지 않습니다."));
  }

  @Override
  public Passport findPassportFromCache(Long userId) {
    try {
      Passport passport = passportRedisRepository.findPassportByUserId(userId);
      return passport;
    } catch (Exception exception) {
      throw exception;
    }
  }

  @Override
  public Passport savePassport(Passport passport) {
    return passportRedisRepository.savePassport(passport);
  }

  @Override
  public boolean existsByEmail(String email) {
    return userJpaRepository.existsByEmail(Email.of(email));
  }

  @Override
  public User save(User user) {
    entityManager.persist(user);
    return user;
  }
}
