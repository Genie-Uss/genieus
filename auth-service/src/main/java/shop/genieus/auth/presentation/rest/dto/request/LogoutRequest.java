package shop.genieus.auth.presentation.rest.dto.request;

import jakarta.validation.constraints.NotBlank;
import shop.genieus.auth.application.in.command.dto.LogoutCommand;

public record LogoutRequest(
    @NotBlank(message = "액세스 토큰이 필요합니다.") String accessToken,
    @NotBlank(message = "리프레쉬 토큰이 필요합니다.") String refreshToken) {
  public LogoutCommand toCommand() {
    return new LogoutCommand(accessToken, refreshToken);
  }
}
