package shop.genieus.auth.infrastructure.support.encoder;

import com.genieus.common.passport.model.Passport;
import com.genieus.common.passport.model.RoleType;
import com.genieus.common.passport.util.PassportUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import shop.genieus.auth.application.out.support.encoder.PassportEncodingPort;

@Component
@RequiredArgsConstructor
@Slf4j(topic = "Base64PassportEncodingAdapter")
public class Base64PassportEncodingAdapter implements PassportEncodingPort {
  private final PassportUtils passportUtils;

  @Override
  public String encode(shop.genieus.auth.domain.model.Passport domainPassport) {
    Passport passport =
        new Passport(domainPassport.getUserId(), /*domainPassport.getRole()*/ RoleType.ADMIN);
    return passportUtils.encode(passport);
  }
}
