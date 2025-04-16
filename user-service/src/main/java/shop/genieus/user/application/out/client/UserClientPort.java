package shop.genieus.user.application.out.client;

import shop.genieus.user.domain.model.entity.User;

public interface UserClientPort {
  void registerAuthUser(User user);
}
