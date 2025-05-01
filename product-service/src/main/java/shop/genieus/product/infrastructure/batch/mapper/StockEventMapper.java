package shop.genieus.product.infrastructure.batch.mapper;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import shop.genieus.product.domain.model.entity.StockEvent;
import shop.genieus.product.domain.model.entity.StockHistory;

@Slf4j
@Component
@RequiredArgsConstructor
public class StockEventMapper {
  private final ObjectMapper objectMapper;

  public List<StockHistory> convertToStockHistories(List<String> eventJsons) {
    return eventJsons.stream()
        .map(this::convertToStockHistory)
        .filter(Objects::nonNull)
        .collect(Collectors.toList());
  }

  public StockHistory convertToStockHistory(String eventJson) {
    try {
      StockEvent event = objectMapper.readValue(eventJson, StockEvent.class);
      return StockHistory.create(
          event.productId(),
          event.orderId(),
          event.quantity(),
          event.type(),
          convertTimestampToLocalDateTime(event.timestamp()));
    } catch (Exception e) {
      log.warn("Redis 값에서 StockEvent 파싱 실패: {}", eventJson, e);
      return null;
    }
  }

  private LocalDateTime convertTimestampToLocalDateTime(long epochMilli) {
    return Instant.ofEpochMilli(epochMilli).atZone(ZoneId.systemDefault()).toLocalDateTime();
  }
}
