package shop.genieus.coupon.infrastructure.persistence.repository;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import shop.genieus.coupon.domain.model.entity.CouponUser;
import shop.genieus.coupon.infrastructure.persistence.dto.IssueCouponCommand;

@Slf4j
@Component
@RequiredArgsConstructor
public class CouponRedisWrite implements ItemWriter<IssueCouponCommand> {
  private final ObjectMapper objectMapper;
  private final CouponUserJpaRepository couponUserJpaRepository;
  private final StringRedisTemplate redisTemplate;

  @Override
  public void write(Chunk<? extends IssueCouponCommand> chunk) throws Exception {
    if (chunk == null || chunk.isEmpty()) return;

    List<CouponUser> entities =
        chunk.getItems().stream().map(dto -> CouponUser.create(dto)).toList();

    try {
      couponUserJpaRepository.bulkInsert(entities);
    } catch (Exception e) {
      log.warn("Bulk insert 실패: {}", e.getMessage());
      for (CouponUser entity : entities) {
        try {
          String failedKey = buildFailedKey();
          redisTemplate.opsForList().rightPush(failedKey, objectMapper.writeValueAsString(entity));
        } catch (JsonProcessingException je) {
          log.warn("실패 데이터 저장 중 JSON 오류: {}", je.getMessage());
        }
      }
    }
  }

  private String buildFailedKey() {
    // 현재 시간을 기준으로 시간별 키 생성
    LocalDateTime now = LocalDateTime.now();
    String formatted = now.format(DateTimeFormatter.ofPattern("yyyyMMddHH"));
    return "coupon:issued:failed:" + formatted;
  }
}
