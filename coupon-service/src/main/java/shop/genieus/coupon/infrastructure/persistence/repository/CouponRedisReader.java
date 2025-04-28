package shop.genieus.coupon.infrastructure.persistence.repository;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.lettuce.core.RedisException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.ItemReader;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import shop.genieus.coupon.infrastructure.persistence.dto.IssueCouponCommand;

@Slf4j
@Component
@RequiredArgsConstructor
public class CouponRedisReader implements ItemReader<IssueCouponCommand> {

  private final StringRedisTemplate redisTemplate;

  private final ObjectMapper objectMapper;

  private static final String COUPON_LIST_KEY = "coupon:issued:list";

  @Override
  public IssueCouponCommand read() throws Exception {
    String json = null;
    try {
      // Redis에 저장된 List에서 하나씩 읽어오기
      json = redisTemplate.opsForList().leftPop(COUPON_LIST_KEY);

      // 더이상 가져올 값이 없으면 null을 반환
      if (json == null) {
        log.info("Redis에서 더 이상 읽을 데이터 없음");
        return null;
      }
      // JSON -> 객체 : 역직렬화
      return objectMapper.readValue(json, IssueCouponCommand.class);
    } catch (JsonProcessingException e) {
      log.error("JSON 처리 실패: {}", e.getMessage());
      String failedKey = buildFailedKey();
      redisTemplate.opsForList().rightPush(failedKey, json);

      // 실패 데이터 ttl 설정: 3일
      Long ttl = redisTemplate.getExpire(failedKey);
      if (ttl == null || ttl == -1) {
        redisTemplate.expire(failedKey, Duration.ofDays(3));
      }
      return null;
    } catch (RedisException e) {
      throw new IllegalArgumentException("Redis 예외 발생");
    }
  }

  private String buildFailedKey() {
    // 현재 시간을 기준으로 시간별 키 생성
    LocalDateTime now = LocalDateTime.now();
    String formatted = now.format(DateTimeFormatter.ofPattern("yyyyMMddHH"));
    return "coupon:issued:failed:" + formatted;
  }
}
