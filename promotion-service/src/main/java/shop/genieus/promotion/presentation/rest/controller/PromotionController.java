package shop.genieus.promotion.presentation.rest.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;
import shop.genieus.promotion.application.in.command.PromotionCommandService;

@RestController
@RequiredArgsConstructor
public class PromotionController {

  private final PromotionCommandService promotionCommandService;

}
