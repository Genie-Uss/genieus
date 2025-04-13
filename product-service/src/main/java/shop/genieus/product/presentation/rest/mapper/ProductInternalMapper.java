package shop.genieus.product.presentation.rest.mapper;

import com.genieus.common.internal.request.StockRequest;
import com.genieus.common.internal.response.ProductClientResponse;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;
import shop.genieus.product.application.in.command.dto.ValidateProductCommand;
import shop.genieus.product.domain.model.entity.Product;

@Component
public class ProductInternalMapper {
  public ProductClientResponse toProductClientResponse(Product product) {
    return new ProductClientResponse(
        product.getProductId(),
        product.getProductName().getValue(),
        product.getProductText(),
        product.getProductPrice().getValue());
  }

  public List<ProductClientResponse> toProductClientResponseList(List<Product> productList) {
    return productList.stream().map(this::toProductClientResponse).collect(Collectors.toList());
  }

  public ValidateProductCommand toValidateStockCommand(StockRequest stockRequest) {
    return new ValidateProductCommand(
        stockRequest.items().stream()
            .map(
                item ->
                    new ValidateProductCommand.StockValidationItem(
                        item.productId(), item.quantity()))
            .toList());
  }
}
