package shop.genieus.payment.domain.event;

import com.genieus.common.event.DomainEvent;

public record PaymentCompleteEvent(Long orderId) implements DomainEvent {}
