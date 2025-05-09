package shop.genieus.order.adapter.out.client.product;

import com.genieus.common.internal.client.ProductInternalClient;
import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(name = "product-service")
public interface ProductFeignClient extends ProductInternalClient {}
