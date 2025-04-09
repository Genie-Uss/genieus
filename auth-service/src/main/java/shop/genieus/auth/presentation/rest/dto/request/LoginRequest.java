package shop.genieus.auth.presentation.rest.dto.request;

import jakarta.validation.constraints.NotBlank;
import shop.genieus.auth.application.in.command.dto.LoginCommand;

public record LoginRequest(
    @NotBlank(message = "아이디를 입력해주세요.") String username,
    @NotBlank(message = "비밀번호를 입력해주세요.") String password) {
  public LoginCommand toCommand() {
    return new LoginCommand(username, password);
  }
}
