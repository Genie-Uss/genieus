package shop.genieus.order.application.in.command;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import shop.genieus.order.application.out.event.OrderInternalEventPort;
import shop.genieus.order.application.out.persistence.OrderCommandPort;
import shop.genieus.order.domain.model.entity.Order;

@Slf4j
@Component
@RequiredArgsConstructor
public class OrderTransactionalSupport {
  private final OrderCommandPort commandPort;
  private final OrderInternalEventPort internalEventPort;

  @Transactional
  public Order saveAndPublish(Order order) {
    Order saved = commandPort.save(order);
    internalEventPort.publishOrderCreated(saved);
    return saved;
  }
}
