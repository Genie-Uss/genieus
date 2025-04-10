package shop.genieus.order.infrastructure.client.mapper;

import com.genieus.common.internal.request.StockRequest;
import com.genieus.common.internal.request.UseCouponRequest;
import com.genieus.common.internal.request.VerifyPromotionRequest;
import com.genieus.common.internal.response.CouponClientResponse;
import com.genieus.common.internal.response.ProductClientResponse;
import com.genieus.common.internal.response.PromotionClientResponse;
import java.time.LocalDateTime;
import java.util.List;
import shop.genieus.order.domain.model.assembler.OrderProductAssembler;
import shop.genieus.order.domain.model.vo.Coupon;
import shop.genieus.order.domain.model.vo.Product;
import shop.genieus.order.domain.model.vo.PromotionProduct;

public class OrderClientMapper {

  public static VerifyPromotionRequest toVerifyPromotionRequest(
      List<OrderProductAssembler> orderProductAssemblers, LocalDateTime orderedAt) {
    List<VerifyPromotionRequest.PromotionItem> promotionItems =
        orderProductAssemblers.stream()
            .map(
                p -> new VerifyPromotionRequest.PromotionItem(p.getProductId(), p.getPromotionId()))
            .toList();
    return new VerifyPromotionRequest(promotionItems, orderedAt);
  }

  public static List<PromotionProduct> toPromotionProducts(List<PromotionClientResponse> response) {
    return response.stream()
        .map(r -> new PromotionProduct(r.productId(), r.discountRate()))
        .toList();
  }

  public static StockRequest toStockRequest(List<OrderProductAssembler> orderProductAssemblers) {
    List<StockRequest.ProductStock> productStocks =
        orderProductAssemblers.stream()
            .map(p -> new StockRequest.ProductStock(p.getProductId(), p.getQuantity()))
            .toList();
    return new StockRequest(productStocks);
  }

  public static List<Product> toProducts(List<ProductClientResponse> response) {
    return response.stream().map(r -> new Product(r.productId(), r.productPrice())).toList();
  }

  public static UseCouponRequest toUseCouponRequest(
      Long userId, Long couponId, LocalDateTime orderedAt) {
    return new UseCouponRequest(userId, couponId, orderedAt);
  }

  public static Coupon toCoupon(CouponClientResponse response) {
    return new Coupon(
        response.couponId(), response.couponDiscountRate(), response.couponMaxPrice());
  }
}
