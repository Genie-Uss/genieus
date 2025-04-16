package shop.genieus.user.infrastructure.persistence.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import shop.genieus.user.domain.model.entity.User;
import shop.genieus.user.domain.model.vo.Email;

public interface UserJpaRepository extends JpaRepository<User, Long> {
  boolean existsByEmail(Email email);
}
