package shop.genieus.auth.infrastructure.persistence;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import shop.genieus.auth.application.out.persistence.AuthPersistencePort;
import shop.genieus.auth.domain.model.entity.User;
import shop.genieus.auth.domain.model.vo.Email;
import shop.genieus.auth.domain.model.vo.TokenCredential;
import shop.genieus.auth.domain.model.vo.TokenId;
import shop.genieus.auth.infrastructure.persistence.repository.TokenRedisRepository;
import shop.genieus.auth.infrastructure.persistence.repository.UserJpaRepository;

@Component
@RequiredArgsConstructor
@Slf4j(topic = "[AuthPersistenceAdapter]")
public class AuthPersistenceAdapter implements AuthPersistencePort {
  private final UserJpaRepository userJpaRepository;
  private final TokenRedisRepository tokenRedisRepository;

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
}
