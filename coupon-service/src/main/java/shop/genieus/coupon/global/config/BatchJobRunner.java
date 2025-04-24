package shop.genieus.coupon.global.config;

import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile("batch")
@RequiredArgsConstructor
public class BatchJobRunner implements CommandLineRunner {

  private final JobLauncher jobLauncher;
  private final Job couponSaveJob;

  @Override
  public void run(String... args) throws Exception {
    JobParameters parameters =
        new JobParametersBuilder()
            .addLong("run.id", System.currentTimeMillis()) // 유니크 ID
            .toJobParameters();

    jobLauncher.run(couponSaveJob, parameters);

    // System.exit(0); // jenkins에서 job이 마무리되어도 로딩 중인 이슈 방지
  }
}
