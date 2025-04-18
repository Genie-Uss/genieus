package shop.genieus.promotion.presentation.rest.controller;

import com.genieus.common.response.ApiResponse;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import shop.genieus.promotion.application.system.PromotionProductService;


@Slf4j
@RestController
@RequestMapping("/api/v1/promotions/update")
@RequiredArgsConstructor
public class PromotionAdminController {

  private final PromotionProductService promotionProductService;

  @PostMapping
  public ResponseEntity<ApiResponse<Void>> updateRate() {
    promotionProductService.findMaxDiscountRateProductsByDate(
        LocalDateTime.now().minusDays(1L));
    log.info("대입 날짜 : {}", LocalDateTime.now().minusDays(1L));

    return ResponseEntity.ok()
        .body(ApiResponse.of(
            3000,
            "당일 최저가 갱신 완료",
            null
        ));
  }
}
