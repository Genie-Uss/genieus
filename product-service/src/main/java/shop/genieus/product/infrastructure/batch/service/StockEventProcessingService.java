package shop.genieus.product.infrastructure.batch.service;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;
import shop.genieus.product.domain.model.entity.StockHistory;
import shop.genieus.product.infrastructure.batch.mapper.StockEventMapper;
import shop.genieus.product.infrastructure.batch.repository.StockEventRedisRepository;
import shop.genieus.product.infrastructure.batch.repository.model.StockEventBatchResult;

@Slf4j
@Service
@RequiredArgsConstructor
public class StockEventProcessingService {
  private static final String EVENT_PREFIX = "event:";
  private final StockEventMapper stockEventMapper;
  private final StockEventRedisRepository stockEventRedisRepository;

  public Iterator<StockHistory> loadStockEventsAfterScore(
      double lastProcessedScore, int batchSize) {
    log.debug(
        "[StockEventProcessingService] 점수 {} 이후의 이벤트 조회, 배치 크기={}", lastProcessedScore, batchSize);

    var eventTuples =
        stockEventRedisRepository.getEventsRangeByScoreWithScores(
            Math.nextUp(lastProcessedScore), batchSize);

    if (eventTuples == null || eventTuples.isEmpty()) {
      log.debug("[StockEventProcessingService] 처리할 이벤트 없음");
      return createEmptyIterator();
    }

    double maxEventScore = findMaxEventScore(eventTuples, lastProcessedScore);

    List<String> eventKeys = extractEventKeys(eventTuples);
    List<String> eventJsons;
    try {
      eventJsons = stockEventRedisRepository.getEventJsonsByKeys(eventKeys);
    } catch (Exception e) {
      log.error("[StockEventProcessingService] 이벤트 JSON 조회 중 오류 발생: {}", e.getMessage(), e);
      return createEmptyIterator();
    }

    List<StockHistory> stockHistories = stockEventMapper.convertToStockHistories(eventJsons);

    return createStockEventBatchResult(stockHistories.iterator(), maxEventScore);
  }

  public Double getLastProcessedEventScore() {
    return stockEventRedisRepository.getLastProcessedEventScore();
  }

  public void saveLastProcessedEventScore(double score) {
    stockEventRedisRepository.setLastProcessedEventScore(score);
  }

  private double findMaxEventScore(
      Set<ZSetOperations.TypedTuple<String>> eventTuples, double defaultScore) {
    if (eventTuples == null || eventTuples.isEmpty()) {
      return defaultScore;
    }
    return eventTuples.stream()
        .mapToDouble(ZSetOperations.TypedTuple::getScore)
        .max()
        .orElse(defaultScore);
  }

  private List<String> extractEventKeys(Set<ZSetOperations.TypedTuple<String>> eventTuples) {
    return eventTuples.stream()
        .map(item -> EVENT_PREFIX + item.getValue())
        .collect(Collectors.toList());
  }

  private Iterator<StockHistory> createEmptyIterator() {
    return Collections.emptyIterator();
  }

  private StockEventBatchResult createStockEventBatchResult(
      Iterator<StockHistory> stockHistoryIterator, double maxEventScore) {
    return new StockEventBatchResult(stockHistoryIterator, maxEventScore);
  }
}
