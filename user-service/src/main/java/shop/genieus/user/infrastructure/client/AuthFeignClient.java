package shop.genieus.user.infrastructure.client;

import com.genieus.common.internal.client.AuthInternalClient;
import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(name = "auth-service")
public interface AuthFeignClient extends AuthInternalClient {}
