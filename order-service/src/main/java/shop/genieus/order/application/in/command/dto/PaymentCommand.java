package shop.genieus.order.application.in.command.dto;

import lombok.Builder;

@Builder
public record PaymentCommand(Long userId, Long orderId, Long couponId) {}
