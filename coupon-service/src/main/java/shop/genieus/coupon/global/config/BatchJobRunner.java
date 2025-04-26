package shop.genieus.coupon.global.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile("batch")
@RequiredArgsConstructor
@Slf4j
public class BatchJobRunner implements CommandLineRunner {

  private final JobLauncher jobLauncher;
  private final Job couponSaveJob;

  @Override
  public void run(String... args) throws Exception {
    JobParameters parameters =
        new JobParametersBuilder()
            .addLong("run.id", System.currentTimeMillis()) // 유니크 ID
            .toJobParameters();

    JobExecution execution = jobLauncher.run(couponSaveJob, parameters);

    // 종료 체크
    if (execution.getStatus() == BatchStatus.COMPLETED) {
      log.info("배치 정상 종료");
      System.exit(0); // jenkins에서 job이 마무리되어도 로딩 중인 이슈 방지
      // Spring Boot context 자체가 종료
    }
  }
}
