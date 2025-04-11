package shop.genieus.auth.presentation.rest.controller;

import com.genieus.common.internal.request.AuthClientRequest;
import com.genieus.common.internal.response.AuthClientResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import shop.genieus.auth.application.in.command.AuthenticationCommandService;
import shop.genieus.auth.application.in.command.PassportCommandService;
import shop.genieus.auth.application.in.command.dto.IssuePassportCommand;
import shop.genieus.auth.application.in.command.dto.ValidateAccessTokenCommand;
import shop.genieus.auth.domain.model.TokenValidationResult;

@RestController
@RequiredArgsConstructor
@RequestMapping("/internal/v1/auth")
public class AuthInternalController {
  private final AuthenticationCommandService authenticationCommandService;
  private final PassportCommandService passportCommandService;

  @PostMapping("/passport")
  public ResponseEntity<AuthClientResponse> issueEncodedPassport(
      @RequestBody final AuthClientRequest request) {
    TokenValidationResult authenticationResult =
        authenticationCommandService.validateAccessToken(
            new ValidateAccessTokenCommand(request.token(), request.method(), request.uri()));

    String serializePassport =
        passportCommandService.issueEncodedPassport(
            IssuePassportCommand.from(authenticationResult));

    return ResponseEntity.ok().body(AuthClientResponse.from(serializePassport));
  }
}
