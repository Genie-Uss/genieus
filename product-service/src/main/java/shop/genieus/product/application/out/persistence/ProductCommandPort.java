package shop.genieus.product.application.out.persistence;

import shop.genieus.product.application.in.command.dto.CreateProductCommand;
import shop.genieus.product.domain.model.entity.Product;

public interface ProductCommandPort {

  Product save(CreateProductCommand command);
}
