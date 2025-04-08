package shop.genieus.auth.global.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import shop.genieus.auth.global.common.GenieusPasswordEncoder;

@Configuration
public class PasswordSecurityConfig {
  @Bean
  public GenieusPasswordEncoder passwordEncoder() {
    return new GenieusPasswordEncoder();
  }
}
