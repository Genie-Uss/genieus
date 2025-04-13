package shop.genieus.product.application.out.persistence;

import java.util.List;
import shop.genieus.product.application.in.command.dto.CreateProductCommand;
import shop.genieus.product.domain.model.entity.Product;

public interface ProductCommandPort {

  Product save(Product product);

  List<Product> findProductsByIds(java.util.List<java.lang.Long> longs);
}
