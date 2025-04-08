package shop.genieus.auth.infrastructure.persistence.util;

import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.scripting.support.ResourceScriptSource;

public class LuaScriptProvider {

  private static final RedisScript<Long> SAVE_REFRESH_TOKEN_SCRIPT;

  static {
    DefaultRedisScript<Long> script = new DefaultRedisScript<>();
    script.setScriptSource(
        new ResourceScriptSource(new ClassPathResource("redis/save-refresh-token.lua")));
    script.setResultType(Long.class);
    SAVE_REFRESH_TOKEN_SCRIPT = script;
  }

  public static RedisScript<Long> getSaveRefreshTokenScript() {
    return SAVE_REFRESH_TOKEN_SCRIPT;
  }
}
