package shop.genieus.auth.infrastructure.support.id;

import java.util.UUID;
import org.springframework.stereotype.Component;
import shop.genieus.auth.application.out.support.id.IdGeneratorPort;

@Component
public class UuidGeneratorAdapter implements IdGeneratorPort {

  @Override
  public String generateUniqueId() {
    return UUID.randomUUID().toString();
  }
}
