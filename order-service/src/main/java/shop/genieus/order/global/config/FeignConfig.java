package shop.genieus.order.global.config;

import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableFeignClients(basePackages = "shop.genieus.order.infrastructure.client.*")
public class FeignConfig {}
