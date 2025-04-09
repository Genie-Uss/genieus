package shop.genieus.auth.domain.model.vo;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import shop.genieus.auth.global.common.AbstractEmail;

@Getter
@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Email extends AbstractEmail {

  @Column(name = "email", length = 20, unique = true, nullable = false)
  private String value; // 필드 재정의 (JPA 매핑을 위함)

  private Email(String value) {
    validate(value); // 공통 검증 로직 사용
    this.value = value;
  }

  public static Email of(String value) {
    return new Email(value);
  }
}
