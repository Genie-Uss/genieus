package shop.genieus.auth.infrastructure.persistence.repository;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;
import shop.genieus.auth.infrastructure.persistence.util.LuaScriptProvider;

@Repository
@RequiredArgsConstructor
public class TokenRedisRepository {
  private static final String REFRESH_TOKEN_PREFIX = "refresh:token:";
  private static final String USER_TOKENS_PREFIX = "user:tokens:";
  private static final String BLACKLIST_PREFIX = "blacklist:";

  private final StringRedisTemplate redisTemplate;

  public void saveRefreshToken(String tokenId, Long userId, String refreshToken, long ttlMillis) {
    List<String> keys = Arrays.asList(REFRESH_TOKEN_PREFIX + tokenId, USER_TOKENS_PREFIX + userId);

    redisTemplate.execute(
        LuaScriptProvider.getSaveRefreshTokenScript(),
        keys,
        refreshToken,
        String.valueOf(ttlMillis),
        tokenId);
  }

  public void addToBlacklist(String tokenId, long ttlMillis) {
    String key = BLACKLIST_PREFIX + tokenId;
    redisTemplate.opsForValue().set(key, "1", ttlMillis, TimeUnit.MILLISECONDS);
  }

  public void removeRefreshToken(String tokenId, Long userId) {
    String refreshTokenKey = REFRESH_TOKEN_PREFIX + tokenId;

    Boolean exists = redisTemplate.hasKey(refreshTokenKey);
    if (Boolean.TRUE.equals(exists)) {
      redisTemplate.delete(refreshTokenKey);
    }
    redisTemplate.opsForSet().remove(USER_TOKENS_PREFIX + userId, tokenId);
  }

  public boolean isBlacklisted(String tokenId) {
    String key = BLACKLIST_PREFIX + tokenId;
    return Boolean.TRUE.equals(redisTemplate.hasKey(key));
  }
}
