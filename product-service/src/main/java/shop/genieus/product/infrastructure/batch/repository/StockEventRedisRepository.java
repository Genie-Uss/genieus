package shop.genieus.product.infrastructure.batch.repository;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ZSetOperations.TypedTuple;
import org.springframework.stereotype.Repository;
import shop.genieus.product.infrastructure.cache.util.ProductLuaScriptProvider;

@Slf4j
@Repository
@RequiredArgsConstructor
public class StockEventRedisRepository {
  private final StringRedisTemplate redisTemplate;
  private final RedisTemplate<String, Double> doubleRedisTemplate;

  @Value("${redis.key.event.processing-queue}")
  private String eventQueueKey;

  @Value("${redis.key.event.stock-last-score}")
  private String eventLastScoreKey;

  public Double getLastProcessedEventScore() {
    return doubleRedisTemplate.opsForValue().get(eventLastScoreKey);
  }

  public void setLastProcessedEventScore(double score) {
    doubleRedisTemplate.opsForValue().set(eventLastScoreKey, score);
  }

  public Set<TypedTuple<String>> getEventsRangeByScoreWithScores(double minScore, int batchSize) {
    return redisTemplate
        .opsForZSet()
        .rangeByScoreWithScores(eventQueueKey, minScore, Double.MAX_VALUE, 0, batchSize);
  }

  public List<String> getEventJsonsByKeys(List<String> eventKeys) {
    if (eventKeys == null || eventKeys.isEmpty()) {
      return Collections.emptyList();
    }

    try {
      return redisTemplate.execute(
          ProductLuaScriptProvider.getMultipleGetHashKeysScript(), eventKeys);
    } catch (Exception e) {
      log.error("Redis Lua 스크립트 실행 중 오류 발생: {}", e.getMessage(), e);
      return Collections.emptyList();
    }
  }
}
