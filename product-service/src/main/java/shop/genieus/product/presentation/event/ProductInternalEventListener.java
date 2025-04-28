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
    log.info("상품 생성 이벤트 수신: productId={}", event.productId());
    internalEventService.onProductCreatedAfterCommit(event);
    log.debug("상품 생성 이벤트 처리 완료: productId={}", event.productId());
  }
}
