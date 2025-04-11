package shop.genieus.product.global.common.auditor;

import java.util.Optional;
import org.springframework.data.domain.AuditorAware;
import org.springframework.stereotype.Component;

@Component
public class AuditorAwareImpl implements AuditorAware<Long> {

  // TODO 나중에 user가 있을 때 변경
  @Override
  public Optional<Long> getCurrentAuditor() {
    return Optional.of(1L);
  }
}