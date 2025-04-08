package shop.genieus.auth.presentation.rest.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class LogoutResponse {
  @JsonProperty
  private final boolean success;

  public static LogoutResponse success() {
    return new LogoutResponse(true);
  }
}
