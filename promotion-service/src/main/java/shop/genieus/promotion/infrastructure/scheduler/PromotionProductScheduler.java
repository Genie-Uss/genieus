package shop.genieus.promotion.infrastructure.scheduler;

import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import shop.genieus.promotion.application.system.PromotionProductService;

@Component
@RequiredArgsConstructor
public class PromotionProductScheduler {

  private final PromotionProductService promotionProductService;

  @Scheduled(cron = "0 0 * * * *")
  public void updateRedisDisCountRate() {
    LocalDateTime date = LocalDateTime.now();
    promotionProductService.findMaxDiscountRateProductsByDate(date);
  }
}
