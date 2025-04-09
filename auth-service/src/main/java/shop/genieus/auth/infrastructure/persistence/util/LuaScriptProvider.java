package shop.genieus.auth.infrastructure.persistence.util;

import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.scripting.support.ResourceScriptSource;

public class LuaScriptProvider {

  private static final RedisScript<Long> SAVE_REFRESH_TOKEN_SCRIPT;
  private static final RedisScript<Long> SAVE_PASSPORT_SCRIPT;
  private static final RedisScript<String> FIND_PASSPORT_BY_USER_ID_SCRIPT;

  static {
    DefaultRedisScript<Long> refreshTokenScript = new DefaultRedisScript<>();
    refreshTokenScript.setScriptSource(
        new ResourceScriptSource(new ClassPathResource("redis/save-refresh-token.lua")));
    refreshTokenScript.setResultType(Long.class);
    SAVE_REFRESH_TOKEN_SCRIPT = refreshTokenScript;

    DefaultRedisScript<Long> passportScript = new DefaultRedisScript<>();
    passportScript.setScriptSource(
        new ResourceScriptSource(new ClassPathResource("redis/save-passport.lua")));
    passportScript.setResultType(Long.class);
    SAVE_PASSPORT_SCRIPT = passportScript;

    DefaultRedisScript<String> findPassportByUserIdScript = new DefaultRedisScript<>();
    findPassportByUserIdScript.setScriptSource(
        new ResourceScriptSource(new ClassPathResource("redis/find-passport-by-user-id.lua")));
    findPassportByUserIdScript.setResultType(String.class);
    FIND_PASSPORT_BY_USER_ID_SCRIPT = findPassportByUserIdScript;
  }

  public static RedisScript<Long> getSaveRefreshTokenScript() {
    return SAVE_REFRESH_TOKEN_SCRIPT;
  }

  public static RedisScript<Long> getSavePassportScript() {
    return SAVE_PASSPORT_SCRIPT;
  }

  public static RedisScript<String> getFindPassportByUserIdScript() {
    return FIND_PASSPORT_BY_USER_ID_SCRIPT;
  }
}
