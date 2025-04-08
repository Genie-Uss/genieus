package shop.genieus.auth.presentation.rest.controller;

import com.genieus.common.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import shop.genieus.auth.application.in.command.AuthenticationCommandService;
import shop.genieus.auth.application.in.command.dto.LoginCommand;
import shop.genieus.auth.application.in.command.dto.LogoutCommand;
import shop.genieus.auth.application.in.command.dto.RefreshCommand;
import shop.genieus.auth.domain.model.TokenPair;
import shop.genieus.auth.presentation.rest.dto.AuthApiResponse;
import shop.genieus.auth.presentation.rest.dto.request.LoginRequest;
import shop.genieus.auth.presentation.rest.dto.request.LogoutRequest;
import shop.genieus.auth.presentation.rest.dto.request.RefreshTokenRequest;
import shop.genieus.auth.presentation.rest.dto.response.LoginResponse;
import shop.genieus.auth.presentation.rest.dto.response.LogoutResponse;
import shop.genieus.auth.presentation.rest.dto.response.RefreshTokenResponse;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth")
public class AuthController {
  private final AuthenticationCommandService commandService;

  @PostMapping("/login")
  public ResponseEntity<ApiResponse<LoginResponse>> login(
      @Valid @RequestBody final LoginRequest request) {
    LoginCommand command = request.toCommand();
    TokenPair tokenPair = commandService.login(command);

    return ResponseEntity.ok().body(AuthApiResponse.ok(LoginResponse.from(tokenPair)));
  }

  @PostMapping("/logout")
  public ResponseEntity<ApiResponse<LogoutResponse>> logout(
      @Valid @RequestBody final LogoutRequest request) {
    LogoutCommand command = request.toCommand();
    commandService.logout(command);

    return ResponseEntity.ok().body(AuthApiResponse.ok(LogoutResponse.success()));
  }

  @PostMapping("/refresh")
  public ResponseEntity<ApiResponse<RefreshTokenResponse>> refreshToken(
      @Valid @RequestBody final RefreshTokenRequest request) {
    RefreshCommand command = request.toCommand(request);
    TokenPair tokenPair = commandService.refresh(command);

    return ResponseEntity.ok().body(AuthApiResponse.ok(RefreshTokenResponse.from(tokenPair)));
  }
}
