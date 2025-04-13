package shop.genieus.order.infrastructure.client.payment;

import com.genieus.common.internal.client.PaymentInternalClient;
import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(name = "payment-service")
public interface PaymentFeignClient extends PaymentInternalClient {}
