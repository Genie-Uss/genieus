package shop.genieus.order.application.in.command.dto;

public record CancelOrderCommand(Long userId, Long orderId) {}
