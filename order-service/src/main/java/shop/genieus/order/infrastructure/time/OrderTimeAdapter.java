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

  /**
   * 현재 Clock 인스턴스를 기준으로 현재 날짜와 시간을 반환합니다.
   *
   * @return 현재 LocalDateTime 객체
   */
  @Override
  public LocalDateTime now() {
    return LocalDateTime.now(clock);
  }

  /**
   * 현재 시간을 UTC 기준으로 에포크 초(1970-01-01T00:00:00Z 이후의 초)로 반환합니다.
   *
   * @return 현재 시간의 에포크 초 값
   */
  @Override
  public long getEpochSecond() {
    return LocalDateTime.now(clock).atZone(ZoneOffset.UTC).toEpochSecond();
  }
}
