package shop.genieus.order.application.out.util;

import java.time.LocalDateTime;

public interface OrderTimePort {
  /****
 * 현재 날짜와 시간을 반환합니다.
 *
 * @return 현재 시각의 LocalDateTime 객체
 */
LocalDateTime now();

  /**
 * 현재 시간을 에포크 초(1970년 1월 1일 00:00:00 UTC 이후의 초)로 반환합니다.
 *
 * @return 현재 시간의 에포크 초 값
 */
long getEpochSecond();
}
