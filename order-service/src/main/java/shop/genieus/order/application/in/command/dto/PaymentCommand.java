package shop.genieus.order.application.in.command.dto;

import com.genieus.common.auth.model.RoleType;
import lombok.Builder;

@Builder
public record PaymentCommand(
    Long userId, RoleType role, Long orderId, Long couponId, String paymentMethod) {}
