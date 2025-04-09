package shop.genieus.auth.global.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.genieus.common.passport.util.PassportUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class PassportEncodingConfig {
  private final ObjectMapper objectMapper;

  @Bean
  public PassportUtils passportUtils() {
    return new PassportUtils(objectMapper);
  }
}
