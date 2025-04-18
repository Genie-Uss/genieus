package shop.genieus.promotion.infrastructure.persistence.repository;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

@Slf4j
@Repository
@RequiredArgsConstructor
public class PromotionRedisHashRepository {
  private final RedisTemplate<String, Integer> redisTemplate;

  public Map<String, Integer> get(String key) {
    HashOperations<String, String, Integer> hashOperations = redisTemplate.opsForHash();
    return hashOperations.entries(key);
  }

  public void save(String hashKey, Map<String, Integer> data, LocalDateTime deleteTime) {
    redisTemplate.opsForHash().putAll(hashKey, data);

    long expireAt = deleteTime
        .atZone(ZoneId.systemDefault())
        .toEpochSecond();
    redisTemplate.expireAt(hashKey, Instant.ofEpochSecond(expireAt));
  }

  public void update(String hashKey, Map<String, Integer> data) {
    redisTemplate.opsForHash().putAll(hashKey, data);
  }

  public Set<String> getHashKeys(String prefix) {
    Set<String> set = redisTemplate.keys(prefix + "*");
    log.info("해시키 조회 : prefix={}, key 개수: {}", prefix, set.size());
    return set;
  }

  public List<Integer> getValues(String hashKey, Collection<String> fields) {
    HashOperations<String, String, Integer> hashOperations = redisTemplate.opsForHash();
    return hashOperations.multiGet(hashKey, fields);
  }
}
