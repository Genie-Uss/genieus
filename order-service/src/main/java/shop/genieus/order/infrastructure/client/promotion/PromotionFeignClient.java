package shop.genieus.order.infrastructure.client.promotion;

import com.genieus.common.internal.client.PromotionInternalClient;
import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(name = "promotion-service")
public interface PromotionFeignClient extends PromotionInternalClient {}
