package shop.genieus.product.application.out.event.internal;

import shop.genieus.product.domain.model.entity.Product;

public interface ProductInternalEventPort {
  void publishProductCreated(Product order);
}
