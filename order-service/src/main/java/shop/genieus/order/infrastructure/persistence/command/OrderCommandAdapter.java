package shop.genieus.order.infrastructure.persistence.command;

import static shop.genieus.order.global.exception.CustomNotFoundException.*;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import shop.genieus.order.application.out.persistence.OrderCommandPort;
import shop.genieus.order.domain.model.entity.Order;
import shop.genieus.order.infrastructure.persistence.command.repository.OrderJpaRepository;

@Component
@RequiredArgsConstructor
public class OrderCommandAdapter implements OrderCommandPort {
  private final OrderJpaRepository orderJpaRepository;

  @Override
  public Order save(Order order) {
    return orderJpaRepository.save(order);
  }

  @Override
  public Order findById(Long orderId) {
    return orderJpaRepository.findById(orderId).orElseThrow(OrderNotFoundException::new);
  }
}
