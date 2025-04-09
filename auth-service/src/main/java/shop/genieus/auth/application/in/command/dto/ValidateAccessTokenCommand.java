package shop.genieus.auth.application.in.command.dto;

public record ValidateAccessTokenCommand(String token, String method, String uri) {}
