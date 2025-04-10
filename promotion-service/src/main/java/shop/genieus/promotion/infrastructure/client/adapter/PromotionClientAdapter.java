package shop.genieus.promotion.infrastructure.client.adapter;

import com.genieus.common.internal.client.ProductInternalClient;
import com.genieus.common.internal.response.ProductClientResponse;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import shop.genieus.promotion.application.out.client.PromotionClientPort;
import shop.genieus.promotion.infrastructure.client.feign.ProductFeignClient;

@Component
@RequiredArgsConstructor
public class PromotionClientAdapter implements PromotionClientPort {

  private final ProductFeignClient productFeignClient;

  @Override
  public List<Long> findProducts(List<Long> productIds) {
    // TODO Feign 응답 값으로 변경
//    List<ProductClientResponse> productClientResponses
//        = productFeignClient.findProductList(productIds);

    return productIds;
  }
}
