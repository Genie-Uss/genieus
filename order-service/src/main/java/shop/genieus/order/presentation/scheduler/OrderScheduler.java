package shop.genieus.order.presentation.scheduler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import shop.genieus.order.application.in.scheduler.OrderSchedulerService;

@Slf4j
@Component
@RequiredArgsConstructor
public class OrderScheduler {
  private final OrderSchedulerService schedulerService;

  @Scheduled(fixedDelayString = "${order.scheduler.poll-interval:30000}")
  public void pollExpiredMessages() {
    schedulerService.popExpiredOrders();
  }
}
