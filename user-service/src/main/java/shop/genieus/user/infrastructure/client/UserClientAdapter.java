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
    log.info("인증 서비스에 사용자 등록 요청: userId={}, email={}", user.getId(), user.getEmail().getValue());
    AuthUserClientRequest request =
        new AuthUserClientRequest(
            user.getId(),
            user.getEmail().getValue(),
            user.getPassword().getValue(),
            user.getRole().getValue());
    try {
      serviceClient.registerAuthUser(request);
      log.info("인증 서비스에 사용자 등록 성공: userId={}", user.getId());
    } catch (Exception e) {
      log.error("인증 서비스에 사용자 등록 실패: userId={}, cause={}", user.getId(), e.getMessage(), e);
      throw e;
    }
  }
}
