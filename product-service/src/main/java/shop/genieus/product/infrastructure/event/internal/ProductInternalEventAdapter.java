package shop.genieus.product.infrastructure.event.internal;

import com.genieus.common.event.product.ProductCreatedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import shop.genieus.product.application.out.event.internal.ProductInternalEventPort;
import shop.genieus.product.domain.model.entity.Product;

@Component
@RequiredArgsConstructor
public class ProductInternalEventAdapter implements ProductInternalEventPort {

  private final ApplicationEventPublisher publisher;

  @Override
  public void publishProductCreated(Product product) {
    ProductCreatedEvent event = new ProductCreatedEvent(product.getProductId());
    publisher.publishEvent(event);
  }
}
