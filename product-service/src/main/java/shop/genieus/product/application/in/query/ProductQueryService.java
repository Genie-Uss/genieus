package shop.genieus.product.application.in.query;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import shop.genieus.product.application.in.command.dto.FindProductCommand;
import shop.genieus.product.application.out.cache.ProductCachePort;
import shop.genieus.product.application.out.persistence.ProductCommandPort;
import shop.genieus.product.domain.model.ProductView;
import shop.genieus.product.domain.model.entity.Product;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ProductQueryService {
  private final ProductCachePort productCachePort;
  private final ProductCommandPort productCommandPort;

  public ProductView getProduct(FindProductCommand command) {
    Long productId = command.productId();
    ProductView view = productCachePort.findProductViewById(productId);

    if (view != null) return view;

    Product product = productCommandPort.findProductById(productId);
    productCachePort.saveProduct(productId, product);
    view = productCachePort.findProductViewById(productId);

    return view;
  }
}
