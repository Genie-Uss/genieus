package shop.genieus.order.application.in.command.dto;

import com.genieus.common.auth.model.RoleType;
import java.util.List;
import lombok.Builder;
import shop.genieus.order.domain.model.assembler.CreateOrderAssembler;
import shop.genieus.order.domain.model.assembler.OrderProductAssembler;

@Builder
public record CreateOrderCommand(
    Long userId, RoleType role, List<OrderProductCommand> orderProductCommands) {

  public record OrderProductCommand(Long productId, Long promotionId, Integer quantity) {}

  public CreateOrderAssembler toAssembler() {
    return CreateOrderAssembler.builder().userId(userId).build();
  }

  public List<OrderProductAssembler> toProductAssembler() {
    return orderProductCommands.stream()
        .map(
            p ->
                OrderProductAssembler.builder()
                    .productId(p.productId)
                    .promotionId(p.promotionId)
                    .quantity(p.quantity)
                    .build())
        .toList();
  }
}
