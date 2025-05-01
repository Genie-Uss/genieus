package shop.genieus.product.infrastructure.batch.context;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import shop.genieus.product.infrastructure.batch.service.StockEventProcessingService;

@Slf4j
@Component
@RequiredArgsConstructor
public class StockEventProcessingContext {
  private final StockEventProcessingService stockEventProcessingService;

  @Getter private double lastProcessedScore = 0;
  private double currentBatchMaxScore = 0;

  @Getter private boolean initialized = false;

  public void initialize() {
    if (initialized) {
      return;
    }

    Double redisOffset = stockEventProcessingService.getLastProcessedEventScore();
    if (redisOffset != null && redisOffset > 0) {
      lastProcessedScore = redisOffset;
    }
    log.debug("[StockEventProcessingContext] lastProcessedScore 초기화: {}", lastProcessedScore);

    currentBatchMaxScore = lastProcessedScore;
    initialized = true;
  }

  public void updateCurrentBatchMaxScore(double score) {
    if (score > currentBatchMaxScore) {
      currentBatchMaxScore = score;
    }
  }

  public void commitCurrentBatch() {
    if (currentBatchMaxScore > lastProcessedScore) {
      log.info(
          "[StockEventProcessingContext] lastProcessedScore 업데이트: {} -> {}",
          lastProcessedScore,
          currentBatchMaxScore);
      lastProcessedScore = currentBatchMaxScore;
    }
  }

  public void close() {
    if (initialized) {
      log.info("[StockEventProcessingContext] 최종 lastProcessedScore 저장: {}", lastProcessedScore);
      stockEventProcessingService.saveLastProcessedEventScore(lastProcessedScore);
      initialized = false;
    }
  }
}
