package shop.genieus.auth.global.common;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class GenieusPasswordEncoder {
  private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

  public String encode(CharSequence rawPassword) {
    return encoder.encode(rawPassword);
  }

  public boolean matches(CharSequence rawPassword, String encodedPassword) {
    return encoder.matches(rawPassword, encodedPassword);
  }
}
