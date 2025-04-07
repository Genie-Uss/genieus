package shop.genieus.user.infrastructure.support.encoder;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import shop.genieus.user.application.out.support.encoder.PasswordEncryptionPort;

@Component
@RequiredArgsConstructor
public class BCryptPasswordEncryptionAdapter implements PasswordEncryptionPort {
  private final org.springframework.security.crypto.password.PasswordEncoder encoder;

  @Override
  public String encode(String rawPassword) {
    return encoder.encode(rawPassword);
  }

  @Override
  public boolean matches(String rawPassword, String encodedPassword) {
    return encoder.matches(rawPassword, encodedPassword);
  }
}
