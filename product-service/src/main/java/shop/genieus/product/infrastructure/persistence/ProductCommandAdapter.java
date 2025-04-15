package shop.genieus.product.infrastructure.persistence;

import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import shop.genieus.product.application.out.persistence.ProductCommandPort;
import shop.genieus.product.domain.model.entity.Product;
import shop.genieus.product.global.exception.ProductNotFoundException;
import shop.genieus.product.infrastructure.persistence.repository.ProductJpaRepository;

@Slf4j
@Component
@RequiredArgsConstructor
public class ProductCommandAdapter implements ProductCommandPort {

  private final ProductJpaRepository productJpaRepository;

  @Override
  public Product save(Product product) {
    return productJpaRepository.save(product);
  }

  @Override
  public Product findProductById(Long productId) {
    return productJpaRepository
        .findById(productId)
        .orElseThrow(
            () -> {
              log.warn("Product id: {}를 찾을 수 없습니다.", productId);
              return new ProductNotFoundException();
            });
  }

  @Override
  public List<Product> findProductsByIds(List<Long> productIds) {
    if (productIds == null || productIds.isEmpty()) return List.of();
    return productJpaRepository.findAvailableProductsByIds(productIds);
  }
}
