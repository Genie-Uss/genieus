package shop.genieus.auth.application.in.command.dto;

public record RefreshCommand(String accessToken, String refreshToken) {}
