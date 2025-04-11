package shop.genieus.product.infrastructure.persistence.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import shop.genieus.product.domain.model.entity.Product;

@Repository
public interface ProductJpaRepository extends JpaRepository<Product, Long> {
}
