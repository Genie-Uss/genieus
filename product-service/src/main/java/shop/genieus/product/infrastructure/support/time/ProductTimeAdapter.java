package shop.genieus.product.infrastructure.support.time;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import org.springframework.stereotype.Component;
import shop.genieus.product.application.out.support.time.ProductTimePort;

@Component
public class ProductTimeAdapter implements ProductTimePort {
  @Override
  public long currentTimeMillis() {
    return System.currentTimeMillis();
  }

  @Override
  public long convertToMillis(LocalDateTime localDateTime) {
    ZonedDateTime zonedDateTime = localDateTime.atZone(ZoneId.systemDefault());
    return zonedDateTime.toInstant().toEpochMilli();
  }

  @Override
  public long convertToStartOfDayToMillis(LocalDateTime localDateTime) {
    return localDateTime
            .toLocalDate()
            .atStartOfDay(ZoneId.systemDefault())
            .toInstant()
            .toEpochMilli();
  }
}
