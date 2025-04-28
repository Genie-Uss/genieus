package shop.genieus.product.presentation.event;

import com.genieus.common.event.product.ProductCreatedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;
import shop.genieus.product.application.in.event.ProductInternalEventService;

@Slf4j
@Component
@RequiredArgsConstructor
public class ProductInternalEventListener {
  private final ProductInternalEventService internalEventService;

  @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
  public void onProductCreatedAfterCommit(ProductCreatedEvent event) {
    internalEventService.onProductCreatedAfterCommit(event);
  }
}
