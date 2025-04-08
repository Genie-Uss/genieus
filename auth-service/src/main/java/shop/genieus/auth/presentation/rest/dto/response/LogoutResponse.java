package shop.genieus.auth.presentation.rest.dto.response;

public record LogoutResponse(boolean success) {
  public static LogoutResponse success() {
    return new LogoutResponse(true);
  }
}
