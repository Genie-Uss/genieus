package shop.genieus.auth.infrastructure.persistence.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import shop.genieus.auth.domain.model.entity.User;
import shop.genieus.auth.domain.model.vo.Email;

public interface UserJpaRepository extends JpaRepository<User, String> {
  @Query("SELECT u FROM User u WHERE u.email = :email AND u.deletedAt IS NULL")
  Optional<User> findByEmailNotDeleted(@Param("email") Email email);

  @Query("SELECT u FROM User u WHERE u.id = :id AND u.deletedAt IS NULL")
  Optional<User> findByIdNotDeleted(@Param("id") Long userId);

  boolean existsByEmail(Email email);
}
