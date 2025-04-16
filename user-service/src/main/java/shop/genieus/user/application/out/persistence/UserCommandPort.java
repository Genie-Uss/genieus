package shop.genieus.user.application.out.persistence;

import shop.genieus.user.domain.model.entity.User;

public interface UserCommandPort {
  boolean existsByEmail(String email);

  User save(User user);
}
