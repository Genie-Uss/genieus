package shop.genieus.auth.infrastructure.support.encoder;

import com.genieus.common.auth.model.Passport;
import com.genieus.common.auth.util.PassportUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import shop.genieus.auth.application.out.support.encoder.PassportEncodingPort;

@Slf4j
@Component
@RequiredArgsConstructor
public class Base64PassportEncodingAdapter implements PassportEncodingPort {
  private final PassportUtils passportUtils;

  @Override
  public String encode(shop.genieus.auth.domain.model.Passport domainPassport) {
    Passport passport = new Passport(domainPassport.getUserId(), domainPassport.getRole());
    return passportUtils.encode(passport);
  }
}
