package shop.genieus.user.presentation.rest.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import shop.genieus.user.application.in.command.dto.CreateUserCommand;

public record CreateUserRequest(
    @Email @NotBlank(message = "이메일은 필수 입력 값입니다.") String email,
    @NotBlank(message = "이름은 필수 입력값입니다") String name,
    @NotBlank(message = "비밀번호는 필수 입력값입니다") String password,
    @NotBlank(message = "비밀번호 확인은 필수 입력값입니다") String confirmedPassword,
    @NotBlank(message = "주민번호 뒷자리 1자리까지의 입력은 필수입니다.") String birthInfo,
    @NotBlank(message = "휴대폰 번호는 필수 입력값입니다") String phoneNumber,
    String address) {
  public CreateUserCommand toCommand() {
    return new CreateUserCommand(
        email, name, password, confirmedPassword, birthInfo, phoneNumber, address);
  }
}
