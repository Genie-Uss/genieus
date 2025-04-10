package shop.genieus.auth.application.in.command;

import java.time.Instant;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import shop.genieus.auth.application.in.command.dto.LoginCommand;
import shop.genieus.auth.application.in.command.dto.LogoutCommand;
import shop.genieus.auth.application.in.command.dto.RefreshCommand;
import shop.genieus.auth.application.in.command.dto.ValidateAccessTokenCommand;
import shop.genieus.auth.application.out.persistence.AuthPersistencePort;
import shop.genieus.auth.application.out.support.encoder.PasswordEncryptionPort;
import shop.genieus.auth.application.out.support.id.IdGeneratorPort;
import shop.genieus.auth.application.out.support.token.AuthTokenPort;
import shop.genieus.auth.domain.model.TokenPair;
import shop.genieus.auth.domain.model.TokenValidationResult;
import shop.genieus.auth.domain.model.entity.User;
import shop.genieus.auth.domain.model.vo.TokenId;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
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

    TokenPair tokenPair = generateAndPersistTokenPair(user.getId());
    log.info("로그인 성공, 유저 로그인 아이디: {}", user.getEmail());

    return tokenPair;
  }

  public void logout(final LogoutCommand command) {
    TokenValidationResult tokenValidationResult =
        validateTokenAndCheckBlacklist(command.accessToken());

    TokenId tokenId = tokenValidationResult.getTokenId();
    revokeTokenPair(command.accessToken(), tokenId, tokenValidationResult.getUserId());
    log.info("로그아웃 성공: TokenId={}", tokenId.value());
  }

  public TokenPair refresh(final RefreshCommand command) {
    String refreshToken = command.refreshToken();
    TokenValidationResult validationResult =
        tokenPort.validateTokenAndExtractId(command.refreshToken());

    Long userId = validationResult.getUserId();
    TokenId oldTokenId = validationResult.getTokenId();

    validateRefreshToken(oldTokenId, refreshToken);
    revokeTokenPair(command.accessToken(), oldTokenId, userId);

    TokenPair newTokenPair = generateAndPersistTokenPair(userId);
    log.info("토큰 갱신 성공- Id: {}", userId);

    return newTokenPair;
  }

  public TokenValidationResult validateAccessToken(ValidateAccessTokenCommand command) {
    return validateTokenAndCheckBlacklist(command.token());
  }

  private TokenPair generateAndPersistTokenPair(Long userId) {
    String tokenId = idGenerator.generateUniqueId();
    TokenPair tokenPair = tokenPort.createTokenPair(tokenId, userId);
    persistencePort.saveRefreshToken(
        tokenPair.getTokenId(), userId, tokenPair.getRefreshTokenCredential());
    return tokenPair;
  }

  private void revokeTokenPair(String accessToken, TokenId tokenId, Long userId) {
    Instant expirationTime = tokenPort.getExpirationTime(accessToken);
    if (expirationTime.isAfter(Instant.now())) {
      persistencePort.addToBlacklist(tokenId, expirationTime);
    }
    persistencePort.removeRefreshToken(tokenId, userId);
  }

  private TokenValidationResult validateTokenAndCheckBlacklist(String token) {
    TokenValidationResult tokenValidationResult = tokenPort.validateTokenAndExtractId(token);
    if (persistencePort.isBlacklisted(tokenValidationResult.getTokenId())) {
      throw new IllegalArgumentException("차단된 JWT 토큰입니다.");
    }
    return tokenValidationResult;
  }

  private void validateRefreshToken(TokenId tokenId, String refreshToken) {
    boolean isValidRefreshToken = persistencePort.isValidRefreshToken(tokenId, refreshToken);
    if (!isValidRefreshToken) {
      throw new IllegalArgumentException("저장된 리프레시 토큰과 일치하지 않습니다. 다시 로그인해주세요.");
    }
  }
}
