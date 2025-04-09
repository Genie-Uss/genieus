package shop.genieus.auth.infrastructure.persistence.repository;

import java.util.Arrays;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;
import shop.genieus.auth.infrastructure.persistence.util.LuaScriptProvider;

@Repository
@RequiredArgsConstructor
public class TokenRedisRepository {
  private static final String REFRESH_TOKEN_PREFIX = "refresh:token:";
  private static final String USER_TOKENS_PREFIX = "user:tokens:";
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
}
