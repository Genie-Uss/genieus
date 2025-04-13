package shop.genieus.promotion.presentation.rest.controller;

import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import shop.genieus.promotion.application.in.query.PromotionQueryService;
import shop.genieus.promotion.domain.model.vo.Product;
import shop.genieus.promotion.presentation.rest.dto.request.VerifyProductsRateRequest;
import shop.genieus.promotion.presentation.rest.dto.response.VerifyProductsRateResponse;

@RestController
@RequestMapping("/internal/v1/promotions")
@RequiredArgsConstructor
public class PromotionInternalController {

  private final PromotionQueryService promotionQueryService;

  @PostMapping("/verify")
  public List<VerifyProductsRateResponse> verifyRates(
     @Valid @RequestBody VerifyProductsRateRequest request) {

    List<Product> productList = promotionQueryService.verifyRates(request.toCommand());
    return productList.stream()
        .map(VerifyProductsRateResponse::from)
        .toList();
  }
}



