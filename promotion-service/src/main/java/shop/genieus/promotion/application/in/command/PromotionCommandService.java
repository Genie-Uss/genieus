package shop.genieus.promotion.application.in.command;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import shop.genieus.promotion.application.in.command.dto.CreatePromotionCommand;
import shop.genieus.promotion.application.in.command.dto.CreatePromotionCommand.PromotionProduct;
import shop.genieus.promotion.application.out.client.PromotionClientPort;
import shop.genieus.promotion.application.out.persistence.PromotionCommandPort;
import shop.genieus.promotion.domain.model.entity.Promotion;

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

  private CreatePromotionCommand getFilteredCommand(CreatePromotionCommand command,
      List<Long> productIdsResponse) {

    Set<Long> validProductIds = new HashSet<>(productIdsResponse);
    List<PromotionProduct> filterProducts
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
                .map(PromotionProduct::productId)
                    .toList();
  }
}
