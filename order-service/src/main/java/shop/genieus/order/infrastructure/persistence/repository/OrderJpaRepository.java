package shop.genieus.order.infrastructure.persistence.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import shop.genieus.order.domain.model.entity.Order;

public interface OrderJpaRepository extends JpaRepository<Order, Long> {}
