package shop.genieus.product.infrastructure.persistence.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import shop.genieus.product.domain.model.entity.Product;
import shop.genieus.product.domain.model.vo.ProductStatus;

@Repository
public interface ProductJpaRepository extends JpaRepository<Product, Long> {
  @Query("SELECT p FROM Product p WHERE p.productId IN :productIds AND p.productStatus = 'ON_SALE' AND p.deletedBy IS NULL")
  List<Product> findAvailableProductsByIds(@Param("productIds") List<Long> productIds);
}
