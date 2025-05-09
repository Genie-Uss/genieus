package shop.genieus.order.adapter.out.client.promotion;

import com.genieus.common.internal.client.PromotionInternalClient;
import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(name = "promotion-service")
public interface PromotionFeignClient extends PromotionInternalClient {}
