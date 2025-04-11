package shop.genieus.promotion.presentation.rest.controller;

import static shop.genieus.promotion.global.constants.Code.*;

import com.genieus.common.response.ApiResponse;
import jakarta.validation.Valid;
import java.net.URI;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import shop.genieus.promotion.application.in.command.PromotionCommandService;
import shop.genieus.promotion.domain.model.entity.Promotion;
import shop.genieus.promotion.presentation.rest.dto.request.CreatePromotionRequest;
import shop.genieus.promotion.presentation.rest.dto.response.CreatePromotionResponse;

@RestController
@RequestMapping("/api/v1/promotions")
@RequiredArgsConstructor
public class PromotionController {

  private final PromotionCommandService promotionCommandService;

  @PostMapping
  public ResponseEntity<ApiResponse<CreatePromotionResponse>> createPromotion(
      @Valid @RequestBody CreatePromotionRequest request) {
    
    Promotion promotion = promotionCommandService.createPromotion(request.toCommand());
    URI uri = makeUri(promotion.getPromotionId());
    
    return ResponseEntity.created(uri).body(
        ApiResponse.of(
            CREATE_PROMOTION_SUCCESS.getCode(),
            CREATE_PROMOTION_SUCCESS.getMessage(),
            CreatePromotionResponse.from(promotion)
        ));
  }

  private URI makeUri(Long promotionId) {
    return ServletUriComponentsBuilder.fromCurrentRequest()
        .path("/{promotionId}")
        .buildAndExpand(promotionId)
        .toUri();
  }
}
