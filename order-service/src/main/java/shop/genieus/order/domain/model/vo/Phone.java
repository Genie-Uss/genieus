package shop.genieus.order.domain.model.vo;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Embeddable
@EqualsAndHashCode
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Phone {
  private static final int MAX_LENGTH = 11;
  private static final String PHONE_REGEX = "^\\d{11}$";

  @Column(name = "phone", length = MAX_LENGTH)
  private String value;

  private Phone(String value) {
    validatePhoneNumber(value);
    this.value = value;
  }

  public static Phone of(String value) {
    return new Phone(value);
  }

  private void validatePhoneNumber(String phoneNumber) {
    if (phoneNumber == null || phoneNumber.isBlank()) {
      throw new IllegalArgumentException("휴대폰 번호는 필수 입력값입니다");
    }

    if (phoneNumber.length() != MAX_LENGTH) {
      throw new IllegalArgumentException("휴대폰 번호는 숫자 " + MAX_LENGTH + "자여야 합니다");
    }

    if (!phoneNumber.matches(PHONE_REGEX)) {
      throw new IllegalArgumentException("휴대폰 번호는 숫자로만 이루어진 11자리여야 합니다");
    }
  }
}
