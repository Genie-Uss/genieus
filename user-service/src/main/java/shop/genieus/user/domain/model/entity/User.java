package shop.genieus.user.domain.model.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.genieus.common.auth.model.RoleType;
import jakarta.persistence.AttributeOverride;
import jakarta.persistence.AttributeOverrides;
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
import shop.genieus.user.domain.model.service.PasswordEncryptionService;
import shop.genieus.user.domain.model.vo.Address;
import shop.genieus.user.domain.model.vo.BirthInfo;
import shop.genieus.user.domain.model.vo.Email;
import shop.genieus.user.domain.model.vo.Name;
import shop.genieus.user.domain.model.vo.Password;
import shop.genieus.user.domain.model.vo.PhoneNumber;
import shop.genieus.user.domain.model.vo.Role;

@Entity
@Getter
@Builder
@Comment("사용자 테이블")
@Table(name = "m_user")
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
  @Comment("사용자 이름")
  private Name name;

  @Embedded
  @Comment("비밀번호 정보")
  @JsonIgnore
  private Password password;

  @Embedded
  @Comment("사용자 권한")
  private Role role;

  @Embedded
  @AttributeOverrides({
    @AttributeOverride(name = "birthdate", column = @Column(name = "birthdate")),
    @AttributeOverride(name = "gender", column = @Column(name = "gender"))
  })
  @Comment("생년월일 및 성별 정보")
  private BirthInfo birthInfo;

  @Embedded
  @Comment("사용자 휴대폰 번호")
  private PhoneNumber phoneNumber;

  @Embedded
  @Comment("사용자 기본 배송지 주소")
  private Address address;

  @Builder.Default
  @Comment("활성화 상태")
  @Column(name = "is_active")
  private boolean isActive = true;

  public static User create(
      String email,
      String name,
      String password,
      PasswordEncryptionService passwordEncryptionService,
      RoleType roleType,
      String residentIdFront,
      String phoneNumber,
      String address) {
    return User.builder()
        .email(Email.of(email))
        .name(Name.of(name))
        .password(Password.of(password, passwordEncryptionService))
        .role(Role.of(roleType))
        .birthInfo(BirthInfo.fromResidentIdFront(residentIdFront))
        .phoneNumber(PhoneNumber.of(phoneNumber))
        .address(address != null ? Address.of(address) : null)
        .build();
  }
}
