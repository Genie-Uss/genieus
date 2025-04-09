package shop.genieus.auth.application.in.command.dto;

public record LogoutCommand(String accessToken, String refreshToken) {}
