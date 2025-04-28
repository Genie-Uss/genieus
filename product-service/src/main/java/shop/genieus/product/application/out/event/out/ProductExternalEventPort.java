package shop.genieus.product.application.out.event.out;

import com.genieus.common.event.product.ProductCreatedEvent;

public interface ProductExternalEventPort {
  void sendProductCreatedEvent(ProductCreatedEvent event);
}
