package shop.genieus.product.infrastructure.cache.util;

import java.util.List;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.scripting.support.ResourceScriptSource;

public class ProductLuaScriptProvider {

  private static final RedisScript<List> VALIDATE_AND_DECREASE_SCRIPT;

  static {
    DefaultRedisScript<List> stockDecreaseScript = new DefaultRedisScript<>();
    stockDecreaseScript.setScriptSource(
        new ResourceScriptSource(new ClassPathResource("redis/validate-and-decrease-script.lua")));
    stockDecreaseScript.setResultType(List.class);
    VALIDATE_AND_DECREASE_SCRIPT = stockDecreaseScript;
  }

  public static RedisScript<List> getValidateAndDecreaseScript() {
    return VALIDATE_AND_DECREASE_SCRIPT;
  }
}
