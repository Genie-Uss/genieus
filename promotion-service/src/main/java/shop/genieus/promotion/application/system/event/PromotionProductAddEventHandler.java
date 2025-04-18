package shop.genieus.promotion.application.system.event;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;
import shop.genieus.promotion.application.system.PromotionProductService;
import shop.genieus.promotion.application.system.dto.UpdateProductCacheCommand;
import shop.genieus.promotion.application.system.event.dto.PromotionProductAddEvent;

@Slf4j
@Component
@RequiredArgsConstructor
public class PromotionProductAddEventHandler {

  private final PromotionProductService promotionProductService;

  @Async
  @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
  public void handle(PromotionProductAddEvent event) {
    try{
      UpdateProductCacheCommand command =
              new UpdateProductCacheCommand(
                      event.getProductId(),
                      event.getPromotionId(),
                      event.getDiscountRate()
              );
      promotionProductService.updateProductDiscountRate(command);
      log.info("개별 상품 업데이트 이벤트 처리 완료, productId: {}, promotionId: {}",
              event.getProductId(), event.getPromotionId());

    } catch (Exception e) {
      log.error("[PromotionProductAddEvent] 이벤트 처리 실패, error: {}", e.getMessage());
    }
  }
}
