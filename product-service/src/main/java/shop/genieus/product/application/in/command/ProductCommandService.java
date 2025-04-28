package shop.genieus.product.application.in.command;

import java.util.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import shop.genieus.product.application.in.command.dto.CreateProductCommand;
import shop.genieus.product.application.in.command.dto.ListProductCommand;
import shop.genieus.product.application.out.cache.ProductCachePort;
import shop.genieus.product.application.out.event.internal.ProductInternalEventPort;
import shop.genieus.product.application.out.persistence.ProductCommandPort;
import shop.genieus.product.domain.model.entity.Product;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class ProductCommandService {

  private final ProductCommandPort commandPort;
  private final ProductCachePort cachePort;
  private final ProductInternalEventPort internalEventPort;

  public Product createProduct(CreateProductCommand command) {
    Product product =
        Product.create(
            command.productName(),
            command.productText(),
            command.productPrice(),
            command.productTotalStock(),
            command.productStatus());

    Product saved = commandPort.save(product);
    cachePort.saveProduct(saved.getProductId(), saved);

    internalEventPort.publishProductCreated(saved);

    return saved;
  }

  public List<Product> findProductListByIds(ListProductCommand command) {
    return commandPort.findProductsByIds(command.productIds());
  }
}
