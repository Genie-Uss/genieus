package shop.genieus.product.infrastructure.event.external;

import com.genieus.common.event.DomainEvent;
import com.genieus.common.event.EventEnvelope;
import com.genieus.common.event.product.ProductCreatedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import shop.genieus.product.application.out.event.out.ProductExternalEventPort;
import shop.genieus.product.infrastructure.event.external.producer.ProductKafkaProducer;

@Component
@RequiredArgsConstructor
public class ProductExternalEventAdapter implements ProductExternalEventPort {
  private final ProductKafkaProducer kafkaProducer;

  @Override
  public void sendProductCreatedEvent(ProductCreatedEvent event) {
    sendProductEvent(event, event.productId());
  }

  private void sendProductEvent(DomainEvent event, Long productId) {
    EventEnvelope<DomainEvent> envelope = EventEnvelope.create(event);
    String key = String.valueOf(productId);
    kafkaProducer.publish(key, envelope);
  }
}
