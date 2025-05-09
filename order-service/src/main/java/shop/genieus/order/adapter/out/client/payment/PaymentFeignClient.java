package shop.genieus.order.adapter.out.client.payment;

import com.genieus.common.internal.client.PaymentInternalClient;
import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(name = "payment-service")
public interface PaymentFeignClient extends PaymentInternalClient {}
