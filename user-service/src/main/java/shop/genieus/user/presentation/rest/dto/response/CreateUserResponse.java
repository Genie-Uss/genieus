package shop.genieus.user.presentation.rest.dto.response;

import java.time.format.DateTimeFormatter;
import shop.genieus.user.domain.model.entity.User;
import shop.genieus.user.domain.model.vo.BirthInfo;

public record CreateUserResponse(
    Long id,
    String email,
    String name,
    String gender,
    String birthdate,
    String phoneNumber,
    String address) {
  public static CreateUserResponse from(User user) {
    BirthInfo birthInfo = user.getBirthInfo();
    return new CreateUserResponse(
        user.getId(),
        user.getEmail().getValue(),
        user.getName().getValue(),
        birthInfo.getGender().getKoreanName(),
        birthInfo.getBirthdate().format(DateTimeFormatter.ISO_LOCAL_DATE),
        user.getPhoneNumber().getValue(),
        user.getAddress().getValue());
  }
}
