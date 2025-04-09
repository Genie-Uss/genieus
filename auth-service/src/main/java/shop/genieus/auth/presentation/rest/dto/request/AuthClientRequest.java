package shop.genieus.auth.presentation.rest.dto.request;

public record AuthClientRequest(String token, String uri, String method) {}
