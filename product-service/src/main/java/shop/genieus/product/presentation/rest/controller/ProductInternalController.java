package shop.genieus.product.presentation.rest.controller;

import com.genieus.common.internal.request.StockRequest;
import com.genieus.common.internal.response.ProductClientResponse;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import shop.genieus.product.application.in.command.ProductCommandService;
import shop.genieus.product.application.in.command.dto.ListProductCommand;
import shop.genieus.product.domain.model.entity.Product;
import shop.genieus.product.presentation.rest.mapper.ProductInternalMapper;

@RestController
@RequiredArgsConstructor
@RequestMapping("/internal/v1/products")
public class ProductInternalController {
  private final ProductCommandService productCommandService;
  private final ProductInternalMapper mapper;

  @GetMapping
  public List<ProductClientResponse> findProductList(@RequestParam List<Long> productIds) {
    List<Product> productList =
        productCommandService.findProductListByIds(new ListProductCommand(productIds));
    return mapper.toProductClientResponseList(productList);
  }

  @PostMapping("/stock")
  public List<ProductClientResponse> useStock(@RequestBody StockRequest stockRequest) {
    List<Product> productList =
        productCommandService.checkStockAvailability(mapper.toValidateStockCommand(stockRequest));

    return mapper.toProductClientResponseList(productList);
  }
}
