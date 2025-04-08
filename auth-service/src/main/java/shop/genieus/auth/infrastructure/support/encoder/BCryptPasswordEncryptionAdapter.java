package shop.genieus.auth.infrastructure.support.encoder;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import shop.genieus.auth.application.out.support.encoder.PasswordEncryptionPort;
import shop.genieus.auth.global.common.GenieusPasswordEncoder;

@Component
@RequiredArgsConstructor
public class BCryptPasswordEncryptionAdapter implements PasswordEncryptionPort {
  private final GenieusPasswordEncoder genieusPasswordEncoder;

  @Override
  public String encode(String rawPassword) {
    return genieusPasswordEncoder.encode(rawPassword);
  }

  @Override
  public boolean matches(String rawPassword, String encodedPassword) {
    return genieusPasswordEncoder.matches(rawPassword, encodedPassword);
  }
}
