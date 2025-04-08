package shop.genieus.auth.application.in.command;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import shop.genieus.auth.application.in.command.dto.LoginCommand;
import shop.genieus.auth.application.out.persistence.AuthPersistencePort;
import shop.genieus.auth.application.out.support.encoder.PasswordEncryptionPort;
import shop.genieus.auth.application.out.support.id.IdGeneratorPort;
import shop.genieus.auth.application.out.support.token.AuthTokenPort;
import shop.genieus.auth.domain.model.TokenPair;
import shop.genieus.auth.domain.model.entity.User;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j(topic = "[AuthenticationCommandService]")
public class AuthenticationCommandService {
  private final AuthPersistencePort persistencePort;
  private final AuthTokenPort tokenPort;
  private final PasswordEncryptionPort encryptionPort;
  private final IdGeneratorPort idGenerator;

  public TokenPair login(final LoginCommand command) {
    User user = persistencePort.findByEmail(command.username());

    if (!user.isActive()) {
      throw new IllegalArgumentException("비활성화된 사용자입니다.");
    }
    if (!user.matchPassword(command.password(), encryptionPort)) {
      throw new IllegalArgumentException("입력하신 아이디 또는 비밀번호가 잘못되었습니다.");
    }

    TokenPair tokenPair = createAndSaveTokenPair(user.getId());
    log.info("로그인 성공, 유저 로그인 아이디: {}", user.getEmail());

    return tokenPair;
  }

  private TokenPair createAndSaveTokenPair(Long userId) {
    String tokenId = idGenerator.generateUniqueId();
    TokenPair tokenPair = tokenPort.createTokenPair(tokenId, userId);
    persistencePort.saveRefreshToken(
        tokenPair.getTokenId(), userId, tokenPair.getRefreshTokenCredential());
    return tokenPair;
  }
}
