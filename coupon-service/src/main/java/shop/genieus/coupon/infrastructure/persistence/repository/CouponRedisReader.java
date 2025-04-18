package shop.genieus.coupon.infrastructure.persistence.repository;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.item.ItemReader;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import shop.genieus.coupon.infrastructure.persistence.dto.IssueCouponCommand;

@Component
@RequiredArgsConstructor
public class CouponRedisReader implements ItemReader<IssueCouponCommand> {
  private final RedisTemplate<String, Object> redisTemplate;
  private final ObjectMapper objectMapper;

  private static final String COUPON_LIST_KEY = "coupon:issued:list";

  @Override
  public IssueCouponCommand read() throws Exception {
    // Redis에 저장된 List에서 하나씩 읽어오기
    String json = (String) redisTemplate.opsForList().leftPop(COUPON_LIST_KEY);

    // 더이상 가져올 값이 없으면 null을 반환
    if (json == null) return null;

    // JSON -> 객체 : 역직렬화
    return objectMapper.readValue(json, IssueCouponCommand.class);
  }
}
