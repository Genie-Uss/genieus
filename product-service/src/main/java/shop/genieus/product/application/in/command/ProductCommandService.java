package shop.genieus.product.application.in.command;

import java.util.*;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import shop.genieus.product.application.in.command.dto.CreateProductCommand;
import shop.genieus.product.application.in.command.dto.ListProductCommand;
import shop.genieus.product.application.in.command.dto.ValidateProductCommand;
import shop.genieus.product.application.out.cache.ProductCachePort;
import shop.genieus.product.application.out.persistence.ProductCommandPort;
import shop.genieus.product.domain.model.ProductView;
import shop.genieus.product.domain.model.entity.Product;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class ProductCommandService {

  private final ProductCommandPort productCommandPort;
  private final ProductCachePort productCachePort;

  public Product createProduct(CreateProductCommand command) {
    Product product =
        Product.create(
            command.productName(),
            command.productText(),
            command.productPrice(),
            command.productTotalStock(),
            command.productStatus());

    Product saved = productCommandPort.save(product);
    productCachePort.saveProduct(saved.getProductId(), saved);
    return saved;
  }

  public List<Product> findProductListByIds(ListProductCommand command) {
    return productCommandPort.findProductsByIds(command.productIds());
  }

  public List<ProductView> checkStockAvailability(ValidateProductCommand command) {
    Map<Long, Integer> requestedQuantities =
        aggregateStockRequests(command.validateProductStocks());

    try {
      List<ProductView> cachedViews =
          productCachePort.validateAndDecreaseStock(requestedQuantities);
      log.info("재고 차감 성공: {}", requestedQuantities);

      return cachedViews;
    } catch (Exception e) {
      log.error("재고 차감 실패: {}", e.getMessage());
      throw e;
    }
  }

  private Map<Long, Integer> aggregateStockRequests(
      List<ValidateProductCommand.StockValidationItem> items) {
    return items.stream()
        .collect(
            Collectors.toMap(
                ValidateProductCommand.StockValidationItem::id,
                ValidateProductCommand.StockValidationItem::quantity,
                Integer::sum));
  }
}
