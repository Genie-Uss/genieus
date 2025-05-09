package shop.genieus.order.global.config;

import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableFeignClients(basePackages = "shop.genieus.order.adapter.out.client.*")
public class FeignConfig {}
