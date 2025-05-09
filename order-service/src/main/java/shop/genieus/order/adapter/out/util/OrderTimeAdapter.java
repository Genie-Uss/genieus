package shop.genieus.order.adapter.out.util;

import java.time.Clock;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import shop.genieus.order.application.out.util.OrderTimePort;

@Component
@RequiredArgsConstructor
public class OrderTimeAdapter implements OrderTimePort {
  private final Clock clock;

  @Override
  public LocalDateTime now() {
    return LocalDateTime.now(clock);
  }

  @Override
  public long getEpochSecond() {
    return now().atZone(clock.getZone()).toEpochSecond();
  }

  @Override
  public long toEpochSecond(LocalDateTime dateTime) {
    return dateTime.atZone(clock.getZone()).toEpochSecond();
  }
}
