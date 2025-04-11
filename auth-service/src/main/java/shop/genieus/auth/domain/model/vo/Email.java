package shop.genieus.auth.domain.model.vo;

import com.genieus.common.auth.model.AbstractEmail;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Email extends AbstractEmail {

  @Column(name = "email", length = 20, unique = true, nullable = false)
  private String value;

  private Email(String value) {
    validate(value);
    this.value = value;
  }

  public static Email of(String value) {
    return new Email(value);
  }
}
