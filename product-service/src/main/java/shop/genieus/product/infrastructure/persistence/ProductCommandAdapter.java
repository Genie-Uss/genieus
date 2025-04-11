package shop.genieus.product.infrastructure.persistence;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import shop.genieus.product.application.in.command.dto.CreateProductCommand;
import shop.genieus.product.application.out.persistence.ProductCommandPort;
import shop.genieus.product.domain.model.entity.Product;
import shop.genieus.product.infrastructure.persistence.repository.ProductJpaRepository;

@Component
@RequiredArgsConstructor
public class ProductCommandAdapter implements ProductCommandPort {

  private final ProductJpaRepository productJpaRepository;

  @Override
  public Product save(CreateProductCommand command) {
    return productJpaRepository.save(create(command));
  }

  private Product create(CreateProductCommand command) {
    return Product.create(
        command.productName(),
        command.productText(),
        command.productPrice(),
        command.productTotalStock(),
        command.productStatus()
    );
  }
}
