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
public class Receiver {

  @Column(name = "receiver_name")
  private String name;

  @Column(name = "receiver_phone")
  private Phone phone;

  @Column(name = "receiver_address")
  private String address;

  private Receiver(String name, String phoneValue, String address) {
    validate(name, phoneValue, address);
    this.name = name;
    this.phone = Phone.of(phoneValue);
    this.address = address;
  }

  public static Receiver of(String name, String phoneValue, String address) {
    return new Receiver(name, phoneValue, address);
  }

  private void validate(String name, String phone, String address) {
    if (address == null || address.isBlank()) {
      throw new IllegalArgumentException("수령인 주소는 필수입니다.");
    }
  }
}
