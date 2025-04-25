package shop.genieus.order.application.out.util;

import java.time.LocalDateTime;

public interface OrderTimePort {
  LocalDateTime now();

  long getEpochSecond();

  long toEpochSecond(LocalDateTime time);
}
