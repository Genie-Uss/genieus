package shop.genieus.gateway.client.dto;

public record AuthClientResponse(
    boolean success, String message, String encodedPassport, String passportHeaderKey) {}
