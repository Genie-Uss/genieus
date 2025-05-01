package shop.genieus.product.infrastructure.batch.service;

import java.util.Collections;
import java.util.Iterator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ItemStreamException;
import org.springframework.batch.item.ItemStreamReader;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import shop.genieus.product.domain.model.entity.StockHistory;
import shop.genieus.product.infrastructure.batch.context.StockEventProcessingContext;
import shop.genieus.product.infrastructure.batch.repository.model.StockEventBatchResult;

@Slf4j
@Component
public class StockEventQueueReader implements ItemStreamReader<StockHistory> {
  private static final String LAST_PROCESSED_SCORE_KEY = "lastProcessedScore";
  private final int batchSize;
  private final StockEventProcessingContext processingContext;
  private final StockEventProcessingService eventProcessingService;

  private Iterator<StockHistory> currentBatchIterator = Collections.emptyIterator();

  public StockEventQueueReader(
      StockEventProcessingService eventProcessingService,
      StockEventProcessingContext processingContext,
      @Value("${batch.product.batchSize}") int batchSize) {
    this.eventProcessingService = eventProcessingService;
    this.processingContext = processingContext;
    this.batchSize = batchSize;
  }

  @Override
  public StockHistory read() {
    if (currentBatchIterator.hasNext()) {
      return currentBatchIterator.next();
    }

    return null;
  }

  @Override
  public void open(ExecutionContext executionContext) throws ItemStreamException {
    if (!processingContext.isInitialized()) {
      processingContext.initialize();
    }

    double lastProcessedScore = processingContext.getLastProcessedScore();
    log.info("[StockEventQueueReader open] lastProcessedScore 초기화: {}", lastProcessedScore);
    loadNextEventBatch();

    executionContext.putDouble(LAST_PROCESSED_SCORE_KEY, lastProcessedScore);
  }

  @Override
  public void update(ExecutionContext executionContext) {
    double lastProcessedScore = processingContext.getLastProcessedScore();
    executionContext.putDouble(LAST_PROCESSED_SCORE_KEY, lastProcessedScore);
  }

  @Override
  public void close() {}

  private void loadNextEventBatch() {
    double lastProcessedScore = processingContext.getLastProcessedScore();

    Iterator<StockHistory> result =
        eventProcessingService.loadStockEventsAfterScore(lastProcessedScore, batchSize);

    if (result instanceof StockEventBatchResult) {
      double maxEventScore = ((StockEventBatchResult) result).getMaxEventScore();
      processingContext.updateCurrentBatchMaxScore(maxEventScore);
      log.debug("[StockEventQueueReader] 최근 처리 이벤트 스코어={}", maxEventScore);
    } else {
      log.debug("[StockEventQueueReader] 처리할 이벤트 없음");
    }

    currentBatchIterator = result;
  }
}
