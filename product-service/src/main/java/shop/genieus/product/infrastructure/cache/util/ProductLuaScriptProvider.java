package shop.genieus.product.infrastructure.cache.util;

import java.util.List;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.scripting.support.ResourceScriptSource;

public class ProductLuaScriptProvider {

  private static final RedisScript<List> VALIDATE_AND_DECREASE_SCRIPT;
  private static final RedisScript<List> RESTORE_STOCK_SCRIPT;

  static {
    DefaultRedisScript<List> stockDecreaseScript = new DefaultRedisScript<>();
    stockDecreaseScript.setScriptSource(
        new ResourceScriptSource(new ClassPathResource("redis/validate-and-decrease-script.lua")));
    stockDecreaseScript.setResultType(List.class);
    VALIDATE_AND_DECREASE_SCRIPT = stockDecreaseScript;

    DefaultRedisScript<List> stockRestoreScript = new DefaultRedisScript<>();
    stockRestoreScript.setScriptSource(
        new ResourceScriptSource(new ClassPathResource("redis/restore-stock-script.lua")));
    stockRestoreScript.setResultType(List.class);
    RESTORE_STOCK_SCRIPT = stockRestoreScript;
  }

  public static RedisScript<List> getValidateAndDecreaseScript() {
    return VALIDATE_AND_DECREASE_SCRIPT;
  }

  public static RedisScript<List> getRestoreStockWithEventsScript() {
    return RESTORE_STOCK_SCRIPT;
  }
}
