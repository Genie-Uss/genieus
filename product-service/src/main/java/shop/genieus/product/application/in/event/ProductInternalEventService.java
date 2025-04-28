package shop.genieus.product.application.in.event;

import com.genieus.common.event.product.ProductCreatedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import shop.genieus.product.application.out.event.out.ProductExternalEventPort;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class ProductInternalEventService {
  private final ProductExternalEventPort externalEventPort;

  public void onProductCreatedAfterCommit(ProductCreatedEvent event) {
    externalEventPort.sendProductCreatedEvent(event);
  }
}
