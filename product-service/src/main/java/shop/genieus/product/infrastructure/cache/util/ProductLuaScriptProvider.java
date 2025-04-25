package shop.genieus.product.infrastructure.cache.util;

import java.util.List;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.scripting.support.ResourceScriptSource;

public class ProductLuaScriptProvider {

  private static final RedisScript<List> VALIDATE_AND_DECREASE_SCRIPT;
  private static final RedisScript<List> DECREASE_USED_STOCK_SCRIPT;
  private static final RedisScript<List> DECREASE_TOTAL_STOCK_SCRIPT;
  private static final RedisScript<List> TOTAL_STOCK_DECREASE_SCRIPT;

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

    DefaultRedisScript<List> decreaseTotalStockScript = new DefaultRedisScript<>();
    decreaseTotalStockScript.setScriptSource(
        new ResourceScriptSource(new ClassPathResource("redis/decrease-total-stock-script.lua")));
    decreaseTotalStockScript.setResultType(List.class);
    DECREASE_TOTAL_STOCK_SCRIPT = decreaseTotalStockScript;

    DefaultRedisScript<List> totalStockDecreaseScript = new DefaultRedisScript<>();
    totalStockDecreaseScript.setScriptSource(
        new ResourceScriptSource(new ClassPathResource("redis/order-completed.lua")));
    totalStockDecreaseScript.setResultType(List.class);
    TOTAL_STOCK_DECREASE_SCRIPT = totalStockDecreaseScript;
  }

  public static RedisScript<List> getValidateAndDecreaseScript() {
    return VALIDATE_AND_DECREASE_SCRIPT;
  }

  public static RedisScript<List> getDecreaseUsedStockScript() {
    return DECREASE_USED_STOCK_SCRIPT;
  }

  public static RedisScript<List> getDecreaseTotalStockScript() {
    return DECREASE_TOTAL_STOCK_SCRIPT;
  }

  public static RedisScript<List> getTotalStockDecreaseScript() {
    return TOTAL_STOCK_DECREASE_SCRIPT;
  }
}
