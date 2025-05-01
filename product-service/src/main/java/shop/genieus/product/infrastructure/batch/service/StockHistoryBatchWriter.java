package shop.genieus.product.infrastructure.batch.service;

import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ItemStream;
import org.springframework.batch.item.ItemStreamException;
import org.springframework.batch.item.ItemWriter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import shop.genieus.product.domain.model.entity.StockHistory;
import shop.genieus.product.infrastructure.batch.context.StockEventProcessingContext;

@Slf4j
@Component
@RequiredArgsConstructor
public class StockHistoryBatchWriter implements ItemWriter<StockHistory>, ItemStream {
  private final JdbcTemplate jdbcTemplate;
  private final StockEventProcessingContext processingContext;

  @Override
  public void open(ExecutionContext executionContext) throws ItemStreamException {
    if (!processingContext.isInitialized()) {
      processingContext.initialize();
    }

    log.info(
        "[StockHistoryBatchWriter open] lastProcessedScore: {}",
        processingContext.getLastProcessedScore());
  }

  @Override
  public void write(Chunk<? extends StockHistory> chunk) throws Exception {
    List<StockHistory> stockHistories = new ArrayList<>(chunk.getItems());

    if (!stockHistories.isEmpty()) {
      log.info("[StockHistoryBatchWriter] 재고 이벤트 히스토리 저장 시작, 사이즈: {}", stockHistories.size());

      jdbcTemplate.batchUpdate(
          "INSERT INTO m_stock_history (product_id, order_id, quantity, type, processed_at, created_at) "
              + "VALUES (?, ?, ? ,? ,?, ?)",
          stockHistories,
          100,
          (PreparedStatement ps, StockHistory stockHistory) -> {
            ps.setLong(1, stockHistory.getProductId());
            ps.setLong(2, stockHistory.getOrderId());
            ps.setInt(3, stockHistory.getQuantity());
            ps.setString(4, stockHistory.getType().toString());
            ps.setObject(5, stockHistory.getProcessedAt());
            ps.setObject(6, stockHistory.getCreatedAt());
          });

      processingContext.commitCurrentBatch();
    }
  }

  @Override
  public void update(ExecutionContext executionContext) {}

  @Override
  public void close() {
    // todo: 비정상 종료도 호출된다고 함..
    processingContext.close();
  }
}
