package shop.genieus.order.infrastructure.client.product;

import com.genieus.common.internal.client.ProductInternalClient;
import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(name = "product-service")
public interface ProductFeignClient extends ProductInternalClient {}
