package shop.genieus.order.adapter.in.api.dto.request;

import static shop.genieus.order.application.in.command.dto.CreateOrderCommand.*;

import com.genieus.common.auth.model.Passport;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import shop.genieus.order.application.in.command.dto.CreateOrderCommand;

public record CreateOrderRequest(
    @Valid @NotNull(message = "주문상품은 필수입니다.") List<OrderProductRequest> orderProductRequests) {

  public record OrderProductRequest(
      @NotNull(message = "상품은 필수입니다.") Long productId,
      @NotNull(message = "프로모션은 필수입니다.") Long promotionId,
      @NotNull(message = "수량은 필수입니다.") Integer quantity) {}

  public CreateOrderCommand toCommand(Passport passport) {
    return builder()
        .userId(passport.getUserId())
        .role(passport.getRole())
        .orderProductCommands(
            orderProductRequests.stream()
                .map(p -> new OrderProductCommand(p.productId, p.promotionId, p.quantity))
                .toList())
        .build();
  }
}
