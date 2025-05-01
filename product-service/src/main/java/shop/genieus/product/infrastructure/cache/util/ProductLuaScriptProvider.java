package shop.genieus.product.infrastructure.cache.util;

import java.util.List;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.scripting.support.ResourceScriptSource;

public class ProductLuaScriptProvider {

  private static final RedisScript<List> VALIDATE_AND_DECREASE_SCRIPT;
  private static final RedisScript<List> DECREASE_USED_STOCK_SCRIPT;
  private static final RedisScript<List> RESTORE_TOTAL_STOCK_SCRIPT;
  private static final RedisScript<List> TOTAL_STOCK_DECREASE_SCRIPT;
  private static final RedisScript<List> MULTIPLE_GET_HASH_KEYS_SCRIPT;

  static {
    DefaultRedisScript<List> stockDecreaseScript = new DefaultRedisScript<>();
    stockDecreaseScript.setScriptSource(
        new ResourceScriptSource(new ClassPathResource("redis/validate-and-decrease-script.lua")));
    stockDecreaseScript.setResultType(List.class);
    VALIDATE_AND_DECREASE_SCRIPT = stockDecreaseScript;

    DefaultRedisScript<List> decreaseUsedStockScript = new DefaultRedisScript<>();
    decreaseUsedStockScript.setScriptSource(
        new ResourceScriptSource(new ClassPathResource("redis/restore-used-stock-script.lua")));
    decreaseUsedStockScript.setResultType(List.class);
    DECREASE_USED_STOCK_SCRIPT = decreaseUsedStockScript;

    DefaultRedisScript<List> restoreTotalStockScript = new DefaultRedisScript<>();
    restoreTotalStockScript.setScriptSource(
        new ResourceScriptSource(new ClassPathResource("redis/restore-total-stock-script.lua")));
    restoreTotalStockScript.setResultType(List.class);
    RESTORE_TOTAL_STOCK_SCRIPT = restoreTotalStockScript;

    DefaultRedisScript<List> totalStockDecreaseScript = new DefaultRedisScript<>();
    totalStockDecreaseScript.setScriptSource(
        new ResourceScriptSource(new ClassPathResource("redis/order-completed.lua")));
    totalStockDecreaseScript.setResultType(List.class);
    TOTAL_STOCK_DECREASE_SCRIPT = totalStockDecreaseScript;

    DefaultRedisScript<List> multiGetHashKeys = new DefaultRedisScript<>();
    multiGetHashKeys.setScriptSource(
        new ResourceScriptSource(new ClassPathResource("redis/multi-get-hash-keys.lua")));
    multiGetHashKeys.setResultType(List.class);
    MULTIPLE_GET_HASH_KEYS_SCRIPT = multiGetHashKeys;
  }

  public static RedisScript<List> getValidateAndDecreaseScript() {
    return VALIDATE_AND_DECREASE_SCRIPT;
  }

  public static RedisScript<List> getDecreaseUsedStockScript() {
    return DECREASE_USED_STOCK_SCRIPT;
  }

  public static RedisScript<List> getRestoreTotalStockScript() {
    return RESTORE_TOTAL_STOCK_SCRIPT;
  }

  public static RedisScript<List> getTotalStockDecreaseScript() {
    return TOTAL_STOCK_DECREASE_SCRIPT;
  }

  public static RedisScript<List> getMultipleGetHashKeysScript() {
    return MULTIPLE_GET_HASH_KEYS_SCRIPT;
  }
}
