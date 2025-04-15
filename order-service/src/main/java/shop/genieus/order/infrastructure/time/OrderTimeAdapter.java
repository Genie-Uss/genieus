package shop.genieus.order.infrastructure.time;

import java.time.Clock;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
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
    return LocalDateTime.now(clock).atZone(ZoneOffset.UTC).toEpochSecond();
  }
}
