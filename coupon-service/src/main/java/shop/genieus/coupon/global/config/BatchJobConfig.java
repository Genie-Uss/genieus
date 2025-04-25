package shop.genieus.coupon.global.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;
import shop.genieus.coupon.infrastructure.persistence.dto.IssueCouponCommand;
import shop.genieus.coupon.infrastructure.persistence.repository.CouponRedisReader;
import shop.genieus.coupon.infrastructure.persistence.repository.CouponRedisWrite;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class BatchJobConfig {
  private final CouponRedisWrite writer;
  private final CouponRedisReader reader;

  @Bean
  public Job couponSaveJob(JobRepository jobRepository, Step saveCouponStep) {
    log.info("✅ read coupon save job");
    return new JobBuilder("couponSaveJob", jobRepository)
        .start(saveCouponStep)
        .incrementer(new RunIdIncrementer())
        .build();
  }

  @Bean
  public Step saveCouponStep(
      JobRepository jobRepository, PlatformTransactionManager transactionManager) {
    log.info("✅ write coupon save job");
    // reader를 100번 호출해서 100개의 데이터를 모은 다음 writer를 호출
    return new StepBuilder("saveCouponStep", jobRepository)
        .<IssueCouponCommand, IssueCouponCommand>chunk(100, transactionManager)
        .reader(reader)
        .writer(writer)
        .build();
  }
}
