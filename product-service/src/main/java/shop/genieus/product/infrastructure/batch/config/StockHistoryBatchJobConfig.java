package shop.genieus.product.infrastructure.batch.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;
import shop.genieus.product.domain.model.entity.StockHistory;
import shop.genieus.product.infrastructure.batch.service.StockEventQueueReader;
import shop.genieus.product.infrastructure.batch.service.StockHistoryBatchWriter;

@Slf4j
@Configuration
public class StockHistoryBatchJobConfig {
  public static final String JOB_NAME = "stockEventProcessingJob";
  public static final String STEP_NAME = "stockEventProcessingStep";
  private final StockEventQueueReader reader;
  private final StockHistoryBatchWriter writer;
  private final int batchSize;

  public StockHistoryBatchJobConfig(
      StockEventQueueReader reader,
      StockHistoryBatchWriter writer,
      @Value("${batch.product.batchSize}") int batchSize) {
    this.reader = reader;
    this.writer = writer;
    this.batchSize = batchSize;
  }

  @Bean
  public Job stockEventProcessingJob(JobRepository jobRepository, Step stockEventProcessingStep) {
    return new JobBuilder(JOB_NAME, jobRepository)
        .start(stockEventProcessingStep)
        .incrementer(new RunIdIncrementer())
        .build();
  }

  @Bean
  public Step stockEventProcessingStep(
      JobRepository jobRepository, PlatformTransactionManager transactionManager) {
    return new StepBuilder(STEP_NAME, jobRepository)
        .<StockHistory, StockHistory>chunk(batchSize, transactionManager)
        .reader(reader)
        .writer(writer)
        .faultTolerant()
        .build();
  }
}
