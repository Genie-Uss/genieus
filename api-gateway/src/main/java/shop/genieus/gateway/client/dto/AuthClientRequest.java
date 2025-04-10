package shop.genieus.gateway.client.dto;

public record AuthClientRequest(String token, String uri, String method) {}
