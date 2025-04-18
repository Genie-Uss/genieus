package shop.genieus.promotion.infrastructure.persistence;

import static shop.genieus.promotion.domain.model.constant.PromotionConstants.*;
import static shop.genieus.promotion.global.constants.Code.*;

import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import shop.genieus.promotion.application.in.command.dto.CreatePromotionCommand;
import shop.genieus.promotion.application.out.persistence.PromotionCommandPort;
import shop.genieus.promotion.domain.model.PromotionForCreate;
import shop.genieus.promotion.domain.model.PromotionProductForCreate;
import shop.genieus.promotion.domain.model.entity.Promotion;
import shop.genieus.promotion.domain.model.entity.PromotionProduct;
import shop.genieus.promotion.global.exception.PromotionException;
import shop.genieus.promotion.infrastructure.persistence.repository.PromotionJpaRepository;

@Slf4j
@Component
@Transactional
@RequiredArgsConstructor
public class PromotionCommandAdapter implements PromotionCommandPort {

  private final PromotionJpaRepository promotionJpaRepository;

  @Override
  public Promotion save(CreatePromotionCommand command) {
    Promotion promotion = createPromotion(command);
    List<PromotionProduct> promotionProductList = createPromotionProducts(command, promotion);
    promotion.setPromotionProducts(promotionProductList);

    promotionJpaRepository.save(promotion);
    log.info("프로모션 저장 성공: {}", promotion);
    return promotion;
  }

  @Override
  @Transactional(readOnly = true)
  public Promotion findById(Long promotionId) {
    return promotionJpaRepository.findByPromotionIdAndDeletedAtIsNull(promotionId)
        .orElseThrow(() -> new PromotionException(PROMOTION_NOT_FOUND));
  }

  @Override
  public Promotion findByName(String promotionName) {
    return promotionJpaRepository.findByPromotionNameAndDeletedAtIsNull(promotionName)
        .orElseThrow(() -> new PromotionException(PROMOTION_NOT_FOUND));
  }

  @Override
  public boolean existByDefaultPromotionProduct(Long productId) {
    return promotionJpaRepository.existByPromotionProductAndDeletedAtIsNull(
        DEFAULT_PROMOTION_NAME, productId);
  }

  private List<PromotionProduct> createPromotionProducts(CreatePromotionCommand command,
      Promotion promotion) {
    return command.promotionProducts()
        .stream()
        .map(dto -> PromotionProduct.create(
            new PromotionProductForCreate(
                dto.productId(),
                dto.promotionProductDiscountRate(),
                dto.promotionProductStatus()
            ),
            promotion
        ))
        .toList();
  }

  private Promotion createPromotion(CreatePromotionCommand command) {
    return Promotion.create(
        new PromotionForCreate(
        command.promotionName(),
        command.promotionStartDate(),
        command.promotionEndDate(),
        command.promotionStatus()
        ));
  }


}
