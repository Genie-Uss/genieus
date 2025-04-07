package shop.genieus.user.domain.model.vo;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Embeddable
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Address {
  private static final int MAX_LENGTH = 255;

  @Column(name = "address", length = MAX_LENGTH)
  private String value;

  private Address(String value) {
    if (value != null) {
      validateAddress(value);
    }
    this.value = value;
  }

  public static Address of(String value) {
    return new Address(value);
  }

  private void validateAddress(String address) {
    if (address.isBlank()) {
      throw new IllegalArgumentException("주소가 비어있습니다");
    }

    if (address.length() > MAX_LENGTH) {
      throw new IllegalArgumentException("주소는 255자를 초과할 수 없습니다");
    }
  }
}
