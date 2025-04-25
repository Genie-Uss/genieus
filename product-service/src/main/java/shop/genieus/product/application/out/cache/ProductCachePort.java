package shop.genieus.product.application.out.cache;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import shop.genieus.product.domain.model.ProductView;
import shop.genieus.product.domain.model.entity.Product;
import shop.genieus.product.domain.model.entity.StockEvent;

public interface ProductCachePort {

  ProductView findProductViewById(Long productId);

  Map<Long, ProductView> findProductViewListByIds(List<Long> productIds);

  void saveProduct(Long productId, Product product);

  void saveProductBatch(List<Product> products);

  List<ProductView> validateAndDecreaseStock(Map<Long, Integer> productQuantities);

  void restoreUsedStock(Map<Long, Integer> releasedQuantities);

  void restoreTotalStock(List<StockEvent> releasedQuantities);

  List<String> totalDecreaseStock(
      Map<Long, Integer> productQuantities, LocalDateTime completedAt, Long orderId);
}
