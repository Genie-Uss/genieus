package shop.genieus.order.infrastructure.persistence.command.repository;

import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import shop.genieus.order.domain.model.entity.Order;

public interface OrderJpaRepository extends JpaRepository<Order, Long> {

  @Modifying(clearAutomatically = true)
  @Query(
      "UPDATE Order o SET o.status = 'ORDER_EXPIRED', o.orderTimeStamp.orderExpiredAt = :expiredAt WHERE o.orderId IN :orderIds")
  void expireAll(
      @Param("orderIds") List<Long> orderIds, @Param("expiredAt") LocalDateTime expiredAt);
}
