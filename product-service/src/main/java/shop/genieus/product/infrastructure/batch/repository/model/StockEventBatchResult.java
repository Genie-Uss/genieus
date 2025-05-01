package shop.genieus.product.infrastructure.batch.repository.model;

import java.util.Iterator;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import shop.genieus.product.domain.model.entity.StockHistory;

@RequiredArgsConstructor
public class StockEventBatchResult implements Iterator<StockHistory>, Iterable<StockHistory> {
  private final Iterator<StockHistory> stockHistoryIterator;
  @Getter private final double maxEventScore;

  @Override
  public boolean hasNext() {
    return stockHistoryIterator.hasNext();
  }

  @Override
  public StockHistory next() {
    return stockHistoryIterator.next();
  }

  @Override
  public Iterator<StockHistory> iterator() {
    return this;
  }
}
