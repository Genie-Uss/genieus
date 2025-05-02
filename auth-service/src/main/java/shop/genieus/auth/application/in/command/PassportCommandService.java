package shop.genieus.auth.application.in.command;

import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import shop.genieus.auth.application.in.command.dto.IssuePassportCommand;
import shop.genieus.auth.application.out.persistence.AuthCommandPort;
import shop.genieus.auth.application.out.support.encoder.PassportEncodingPort;
import shop.genieus.auth.application.out.support.id.IdGeneratorPort;
import shop.genieus.auth.domain.model.Passport;
import shop.genieus.auth.domain.model.entity.User;

@Slf4j
@Service
@RequiredArgsConstructor
public class PassportCommandService {
  private final PassportEncodingPort passportEncodingPort;
  private final AuthCommandPort commandPort;
  private final IdGeneratorPort idGeneratorPort;

  public String issueEncodedPassport(IssuePassportCommand command) {
    Long userId = command.userId();

    Passport cachedPassport = checkCachedPassport(userId);
    if (cachedPassport != null) {
      return serializePassport(cachedPassport);
    }

    User user = commandPort.findByUserId(userId);
    log.debug("passport 발급을 요청한 유저: {}", user.toString());
    Passport passport = createAndSavePassport(user);

    log.info("사용자 Id-[{}]에 대한 패스포트가 성공적으로 발급되었습니다.", userId);
    return serializePassport(passport);
  }

  private Passport checkCachedPassport(Long userId) {
    try {
      Passport cachedPassport = commandPort.findPassportFromCache(userId);
      if (cachedPassport != null) {
        return cachedPassport;
      }
    } catch (Exception exception) {
      log.error(exception.getMessage());
    }
    return null;
  }

  private Passport createAndSavePassport(User user) {
    String sessionId = idGeneratorPort.generateUniqueId();
    LocalDateTime now = LocalDateTime.now();
    Passport passport = Passport.create(sessionId, user.getId(), user.getRole().getValue(), now);

    return commandPort.savePassport(passport);
  }

  private String serializePassport(Passport passport) {
    return passportEncodingPort.encode(passport);
  }
}
