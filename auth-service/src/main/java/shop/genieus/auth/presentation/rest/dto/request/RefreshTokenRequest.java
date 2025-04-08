package shop.genieus.auth.presentation.rest.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import shop.genieus.auth.application.in.command.dto.RefreshCommand;

public record RefreshTokenRequest(
    @NotBlank(message = "액세스 토큰이 필요합니다.") String accessToken,
    @NotBlank(message = "리프레쉬 토큰이 필요합니다.") String refreshToken) {
  public RefreshCommand toCommand(@Valid RefreshTokenRequest request) {
    return new RefreshCommand(accessToken, refreshToken);
  }
}
