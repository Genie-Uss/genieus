package shop.genieus.promotion.global.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import shop.genieus.promotion.global.config.auditor.PassportAuditorAware;

@Configuration
@EnableJpaAuditing(auditorAwareRef = "passportAuditorAware")
public class JpaConfig {
  @Bean
  public AuditorAware<Long> passportAuditorAware() {
    return new PassportAuditorAware();
  }
}