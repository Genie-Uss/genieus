package shop.genieus.user.global.config;

import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableFeignClients(basePackages = "shop.genieus.user.infrastructure.client")
public class FeignConfig {}
