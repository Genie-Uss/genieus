package shop.genieus.order.adapter.out.client.coupon;

import com.genieus.common.internal.client.CouponInternalClient;
import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(name = "coupon-service")
public interface CouponFeignClient extends CouponInternalClient {}
