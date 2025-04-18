package shop.genieus.promotion.presentation.event.consumer;

import com.genieus.common.event.EventEnvelope;
import com.genieus.common.event.annotation.EventTypeMapping;
import com.genieus.common.event.annotation.FallbackMapping;
import com.genieus.common.event.product.ProductCreatedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import shop.genieus.promotion.application.in.command.PromotionCommandService;
import shop.genieus.promotion.application.system.event.dto.PromotionProductAddEvent;
import shop.genieus.promotion.domain.model.entity.PromotionProduct;

@Slf4j
@Component
@RequiredArgsConstructor
public class ProductEventHandler {

    private final PromotionCommandService promotionCommandService;
    private final ApplicationEventPublisher publisher;

    @Transactional
    @EventTypeMapping(topic = "product-events")
    public void handleProductCreatedEvent(ProductCreatedEvent event) {
        log.info("상품 생성 이벤트 수신, event: {}", event);
        Long productId = event.productId();
        PromotionProduct promotionProduct = promotionCommandService.addProduct(productId);

        // Redis 최저가 업데이트 이벤트 발행
        PromotionProductAddEvent addEvent =
                new PromotionProductAddEvent(
                        this,
                        promotionProduct.getProductId(),
                        promotionProduct.getPromotion().getPromotionId(),
                        promotionProduct.getPromotionProductDiscountRate().getValue()
                );

        publisher.publishEvent(addEvent);
        log.info("상품 추가 내부 이벤트 발행 {}", addEvent);
    }

    @FallbackMapping(topic = "product-events", eventType = "ProductCreatedEvent")
    public void fallbackProductCreatedEvent(EventEnvelope<ProductCreatedEvent> envelope, Throwable ex) {
        log.info("상품 생성 이벤트 처리 실패 fallback, eventId: {}, event: {}, error: {}",
                envelope.getEventId(), envelope.getEvent(), ex.getMessage());
    }
}
