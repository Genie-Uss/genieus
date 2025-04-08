package shop.genieus.auth.domain.model.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;
import shop.genieus.auth.domain.model.service.PasswordEncryptionService;
import shop.genieus.auth.domain.model.vo.Email;
import shop.genieus.auth.domain.model.vo.Password;
import shop.genieus.auth.domain.model.vo.Role;
import shop.genieus.auth.global.common.RoleType;

@Entity
@Getter
@Builder
@Comment("사용자 테이블")
@Table(name = "m_auth_user")
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User extends BaseEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "user_id")
  private Long id;

  @Embedded
  @Comment("사용자 로그인 아이디(이메일)")
  private Email email;

  @Embedded
  @Comment("비밀번호 정보")
  @JsonIgnore
  private Password password;

  @Embedded
  @Comment("사용자 권한")
  private Role role;

  @Builder.Default
  @Comment("활성화 상태")
  @Column(name = "is_active")
  private boolean isActive = true;

  public static User create(
      String email,
      String password,
      PasswordEncryptionService passwordEncryptionService,
      RoleType roleType) {
    return User.builder()
        .email(Email.of(email))
        .password(Password.of(password, passwordEncryptionService))
        .role(Role.of(roleType))
        .build();
  }
}
