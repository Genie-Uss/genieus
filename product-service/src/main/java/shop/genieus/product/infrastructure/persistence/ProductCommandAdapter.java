package shop.genieus.product.infrastructure.persistence;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import shop.genieus.product.application.in.command.dto.CreateProductCommand;
import shop.genieus.product.application.out.persistence.ProductCommandPort;
import shop.genieus.product.domain.model.entity.Product;
import shop.genieus.product.domain.model.vo.ProductStatus;
import shop.genieus.product.infrastructure.persistence.repository.ProductJpaRepository;

@Component
@RequiredArgsConstructor
public class ProductCommandAdapter implements ProductCommandPort {

  private final ProductJpaRepository productJpaRepository;

  @Override
  public Product save(Product product) {
    return productJpaRepository.save(product);
  }

  @Override
  public List<Product> findProductsByIds(List<Long> productIds) {
    if (productIds == null || productIds.isEmpty()) {
      return List.of();
    }
    return productJpaRepository.findAvailableProductsByIds(productIds);
  }
}
