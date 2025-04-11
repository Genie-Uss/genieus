package shop.genieus.order.infrastructure.event;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import shop.genieus.order.application.out.event.OrderEventSendPort;
import shop.genieus.order.domain.model.entity.Order;

@Component
@RequiredArgsConstructor
public class OrderEventSendAdapter implements OrderEventSendPort {
  @Override
  public void sendOrderCanceledEvent(Order order) {}
}
