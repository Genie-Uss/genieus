package shop.genieus.coupon.infrastructure.persistence.repository;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Repository;
import shop.genieus.coupon.application.in.command.dto.IssueCouponCommand;
import shop.genieus.coupon.domain.model.entity.Coupon;
import shop.genieus.coupon.domain.model.vo.CouponUseStatus;

@Slf4j
@Repository
@RequiredArgsConstructor
public class RedisCouponRepository {
  private final ObjectMapper objectMapper;
  private final RedisTemplate<String, Long> redisTemplate;

  // todo.Redis에 재고 세팅 로직은 언제 수행하는 게 좋지? 쿠폰 생길 때?

  public void createCouponUser(Coupon coupon, Long userId) {
    // 1. Lua 스크립트 로드
    DefaultRedisScript<Long> luaScript = new DefaultRedisScript<>();
    luaScript.setLocation(new ClassPathResource("redis/issue_coupon.lua"));
    luaScript.setResultType(Long.class);

    // 2. Lua 스크립트에 전달할 키 설정
    String issuedKey = "coupon" + coupon.getCouponId() + ":user" + userId;
    String stockKey = "coupon:stock:" + coupon.getCouponId();
    String listKey = "coupon:issued:list";

    List<String> keys = Arrays.asList(issuedKey, stockKey, listKey);

    // 3. Lua Script에 전달할 value 값 설정 => list에 넣을 dto (json형태)
    IssueCouponCommand couponCommand =
        new IssueCouponCommand(
            userId,
            coupon.getCouponId(),
            LocalDateTime.now(),
            coupon.getCouponExpiredDate().getValue(),
            CouponUseStatus.AVAILABLE);
    String json = null;
    try {
      json = objectMapper.writeValueAsString(couponCommand);

    } catch (JsonProcessingException e) {
      throw new IllegalArgumentException(e);
    }

    // 4. Lua 스크립트 실행
    Long result = redisTemplate.execute(luaScript, keys, json);

    if (result == null) {
      throw new IllegalArgumentException("Redis Lua Script 실행 실패");
    }

    // 5. 반환 결과에 따른 로직 처리
    switch (result.intValue()) {
      case 1:
        log.info("쿠폰 발급 성공!");
        break;
      case -1:
        throw new IllegalArgumentException("이미 쿠폰이 발급된 사용자입니다.");
      case -2:
        throw new IllegalArgumentException("쿠폰이 모두 소진되었습니다.");
    }
  }
}
