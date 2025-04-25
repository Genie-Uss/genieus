package shop.genieus.product.application.out.support.time;

import java.time.LocalDateTime;

public interface ProductTimePort {
  long currentTimeMillis();

  long convertToMillis(LocalDateTime localDateTime);
}
