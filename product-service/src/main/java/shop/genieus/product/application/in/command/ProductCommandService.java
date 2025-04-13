package shop.genieus.product.application.in.command;

import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import shop.genieus.product.application.in.command.dto.CreateProductCommand;
import shop.genieus.product.application.in.command.dto.ListProductCommand;
import shop.genieus.product.application.in.command.dto.ValidateProductCommand;
import shop.genieus.product.application.out.persistence.ProductCommandPort;
import shop.genieus.product.domain.model.entity.Product;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class ProductCommandService {
  private final ProductCommandPort productCommandPort;

  public Product createProduct(CreateProductCommand command) {
    return productCommandPort.save(
        Product.create(
            command.productName(),
            command.productText(),
            command.productPrice(),
            command.productTotalStock(),
            command.productStatus()));
  }

  public List<Product> findProductListByIds(ListProductCommand command) {
    return productCommandPort.findProductsByIds(command.productIds());
  }

  public List<Product> checkStockAvailability(ValidateProductCommand command) {
    return productCommandPort.findProductsByIds(
        command.validateProductStocks().stream().map(item -> item.id()).toList());
  }
}
