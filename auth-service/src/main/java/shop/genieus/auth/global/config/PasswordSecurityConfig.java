package shop.genieus.auth.global.config;

import com.genieus.common.auth.util.GenieusPasswordEncoder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class PasswordSecurityConfig {
  @Bean
  public GenieusPasswordEncoder passwordEncoder() {
    return new GenieusPasswordEncoder();
  }
}
