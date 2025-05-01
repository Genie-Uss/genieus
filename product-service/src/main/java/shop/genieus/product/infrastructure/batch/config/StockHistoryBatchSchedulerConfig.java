package shop.genieus.product.infrastructure.batch.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

@Slf4j
@Configuration
@EnableScheduling
@RequiredArgsConstructor
public class StockHistoryBatchSchedulerConfig {
  private static final String PARAM_EXECUTION_TIME = "executionTime";

  private final JobLauncher jobLauncher;
  private final Job stockEventProcessingJob;

  @Scheduled(cron = "${batch.product.cron}")
  public void launchStockEventProcessingJob() throws Exception {
    log.info("[Job Build] 재고 이벤트 히스토리 배치 작업 시작");
    JobParameters jobParameters =
        new JobParametersBuilder()
            .addLong(PARAM_EXECUTION_TIME, System.currentTimeMillis())
            .toJobParameters();

    try {
      jobLauncher.run(stockEventProcessingJob, jobParameters);
      log.info("[Job Run Success] 재고 이벤트 히스토리 배치 작업 종료");
    } catch (Exception e) {
      log.info("[Job Run Fail] 재고 이벤트 히스토리 처리 중 오류가 발생: {}", e.getMessage());
      throw e;
    }
  }
}
