package shop.genieus.promotion.application.system.runner;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import shop.genieus.promotion.application.system.PromotionProductService;

import java.time.LocalDateTime;

@Slf4j
@Component
@RequiredArgsConstructor
public class TodayLowestRateRunner implements ApplicationRunner {

    private final PromotionProductService promotionProductService;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        try {
            LocalDateTime checkDay = LocalDateTime.now().minusDays(1);
            promotionProductService.findMaxDiscountRateProductsByDate(checkDay);
            log.info("당일 최저가 갱신 완료");

        } catch (Exception e) {
            log.info("당일 최저가 갱신 실패");
        }
    }
}
