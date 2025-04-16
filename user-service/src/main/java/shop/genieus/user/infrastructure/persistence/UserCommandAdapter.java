package shop.genieus.user.infrastructure.persistence;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import shop.genieus.user.application.out.persistence.UserCommandPort;
import shop.genieus.user.domain.model.entity.User;
import shop.genieus.user.domain.model.vo.Email;
import shop.genieus.user.infrastructure.persistence.repository.UserJpaRepository;

@Slf4j
@Component
@RequiredArgsConstructor
public class UserCommandAdapter implements UserCommandPort {
  private final UserJpaRepository userJpaRepository;

  @Override
  public boolean existsByEmail(String email) {
    return userJpaRepository.existsByEmail(Email.of(email));
  }

  @Override
  public User save(User user) {
    return userJpaRepository.save(user);
  }
}
