package shop.genieus.promotion.application.in.command;

import static shop.genieus.promotion.domain.model.constant.PromotionConstants.*;
import static shop.genieus.promotion.global.constants.Code.*;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import shop.genieus.promotion.application.in.command.dto.CreatePromotionCommand;
import shop.genieus.promotion.application.in.command.dto.CreatePromotionCommand.PromotionProductDto;
import shop.genieus.promotion.application.out.client.PromotionClientPort;
import shop.genieus.promotion.application.out.persistence.PromotionCommandPort;
import shop.genieus.promotion.domain.model.PromotionProductForCreate;
import shop.genieus.promotion.domain.model.entity.Promotion;
import shop.genieus.promotion.domain.model.entity.PromotionProduct;
import shop.genieus.promotion.domain.model.vo.PromotionProductStatus;
import shop.genieus.promotion.global.exception.PromotionException;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class PromotionCommandService {

  private final PromotionCommandPort promotionCommandPort;
  private final PromotionClientPort promotionClientPort;

  public Promotion createPromotion(CreatePromotionCommand command) {
    List<Long> productIds = getProductIds(command);
    List<Long> productIdsResponse = promotionClientPort.findProducts(productIds);

    if(productIds.size() == productIdsResponse.size()) {
      return promotionCommandPort.save(command);
    }

    CreatePromotionCommand filteredCommand = getFilteredCommand(command, productIdsResponse);
    return promotionCommandPort.save(filteredCommand);
  }

  public PromotionProduct addProduct(Long productId) {
    boolean existProduct = promotionCommandPort.existByDefaultPromotionProduct(productId);
    log.info("exist product: {}", existProduct);

    if(existProduct) {
      log.info("이미 존재하는 상품, productId: {}", productId);
      throw new PromotionException(ALREADY_PROMOTION_PRODUCT);
    }

    Promotion promotion = promotionCommandPort.findByName(DEFAULT_PROMOTION_NAME);
    PromotionProduct promotionProduct = makeDefaultPromotionProduct(productId, promotion);
    promotion.getPromotionProducts().add(promotionProduct);;
    log.info("프로모션 상품 생성 : productId: {}, promotionId: {}",
        promotionProduct.getProductId(), promotion.getPromotionId());
    return promotionProduct;
  }

  private PromotionProduct makeDefaultPromotionProduct(
      Long productId, Promotion promotion) {
    PromotionProductForCreate productDto = new PromotionProductForCreate(
        productId, 0, PromotionProductStatus.ON_SALE);
    PromotionProduct promotionProduct = PromotionProduct.create(productDto, promotion);
    log.info("추가할 상품 생성, productId: {}, 할인율: {}, 판매 상태: {}",
        productId, 0, PromotionProductStatus.READY);
    return promotionProduct;
  }

  private CreatePromotionCommand getFilteredCommand(CreatePromotionCommand command,
      List<Long> productIdsResponse) {

    Set<Long> validProductIds = new HashSet<>(productIdsResponse);
    List<PromotionProductDto> filterProducts
        = command.promotionProducts().stream()
        .filter(p -> validProductIds.contains(p.productId()))
        .toList();

    return new CreatePromotionCommand(
        command.promotionName(),
        command.promotionStartDate(),
        command.promotionEndDate(),
        command.promotionStatus(),
        filterProducts
    );
  }

  private List<Long> getProductIds(CreatePromotionCommand command) {
    return command.promotionProducts()
            .stream()
                .map(PromotionProductDto::productId)
                    .toList();
  }
}
