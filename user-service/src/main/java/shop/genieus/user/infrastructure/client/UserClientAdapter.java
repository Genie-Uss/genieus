package shop.genieus.user.infrastructure.client;

import com.genieus.common.internal.request.AuthUserClientRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import shop.genieus.user.application.out.client.UserClientPort;
import shop.genieus.user.domain.model.entity.User;

@Slf4j
@Component
@RequiredArgsConstructor
public class UserClientAdapter implements UserClientPort {
  private final AuthServiceClient serviceClient;

  @Override
  public void registerAuthUser(User user) {
    AuthUserClientRequest request =
        new AuthUserClientRequest(
            user.getId(),
            user.getEmail().getValue(),
            user.getPassword().getValue(),
            user.getRole().getValue());
    serviceClient.registerAuthUser(request);
  }
}
