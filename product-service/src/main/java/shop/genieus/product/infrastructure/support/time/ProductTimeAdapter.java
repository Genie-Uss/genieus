package shop.genieus.product.infrastructure.support.time;

import org.springframework.stereotype.Component;
import shop.genieus.product.application.out.support.time.ProductTimePort;

@Component
public class ProductTimeAdapter implements ProductTimePort {
  @Override
  public long currentTimeMillis() {
    return System.currentTimeMillis();
  }
}
