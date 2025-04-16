package shop.genieus.user.presentation.rest;

import com.genieus.common.response.ApiResponse;
import jakarta.validation.Valid;
import java.net.URI;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import shop.genieus.user.application.in.command.UserCommandService;
import shop.genieus.user.domain.model.entity.User;
import shop.genieus.user.presentation.rest.dto.UserApiResponse;
import shop.genieus.user.presentation.rest.dto.request.CreateUserRequest;
import shop.genieus.user.presentation.rest.dto.response.CreateUserResponse;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/users")
public class UserController {
  private final UserCommandService commandService;

  @PostMapping
  public ResponseEntity<ApiResponse<CreateUserResponse>> createUser(
      @Valid @RequestBody final CreateUserRequest request) {
    User user = commandService.createUser(request.toCommand());
    CreateUserResponse response = CreateUserResponse.from(user);

    return ResponseEntity.created(generateUri(response.id())).body(UserApiResponse.ok(response));
  }

  private URI generateUri(Long id) {
    return ServletUriComponentsBuilder.fromCurrentRequest()
        .path("/{id}")
        .buildAndExpand(id)
        .toUri();
  }
}
