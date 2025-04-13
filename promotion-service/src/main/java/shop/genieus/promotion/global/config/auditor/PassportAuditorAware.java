package shop.genieus.promotion.global.config.auditor;

import com.genieus.common.auth.context.PassportContext;
import com.genieus.common.auth.model.Passport;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.AuditorAware;

@Slf4j
public class PassportAuditorAware implements AuditorAware<Long> {
  private static final Long DEFAULT_AUDITOR = -1L;

  @Override
  public Optional<Long> getCurrentAuditor() {
    try {
      Passport passport = PassportContext.getPassport();

      if (passport == null) {
        log.debug("패스포트 컨텍스트에서 패스포트를 찾을 수 없습니다.");
        return Optional.of(DEFAULT_AUDITOR);
      }

      return Optional.ofNullable(passport.getUserId()).or(() -> Optional.of(DEFAULT_AUDITOR));
    } catch (Exception e) {
      log.warn("패스포트에서 현재 감사를 확인하는 중 오류 발생: ", e);
      return Optional.of(DEFAULT_AUDITOR);
    }
  }
}