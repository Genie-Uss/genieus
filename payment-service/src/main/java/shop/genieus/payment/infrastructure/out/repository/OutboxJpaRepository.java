package shop.genieus.payment.infrastructure.out.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import shop.genieus.payment.infrastructure.out.entity.Outbox;

public interface OutboxJpaRepository extends JpaRepository<Outbox, Long> {

  List<Outbox> findAllByIsPublishedFalse();
}
