package shop.genieus.order.application.in.scheduler;

import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import shop.genieus.order.application.in.command.OrderCommandService;
import shop.genieus.order.application.in.command.dto.ExpireOrderCommand;
import shop.genieus.order.application.out.persistence.OrderDelayQueuePort;
import shop.genieus.order.application.out.util.OrderTimePort;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderSchedulerService {
  private final OrderTimePort timePort;
  private final OrderDelayQueuePort delayQueuePort;
  private final OrderCommandService commandService;

  public void popExpiredOrders() {
    long epochSecond = timePort.getEpochSecond();
    List<Long> expiredIds;
    try {
      expiredIds = delayQueuePort.popExpiredOrders(epochSecond);
    } catch (Exception e) {
      log.error("만료된 주문 조회 중 오류 발생: {}", e.getMessage(), e);
      return;
    }
    if (expiredIds.isEmpty()) {
      return;
    }
    ExpireOrderCommand command = ExpireOrderCommand.of(expiredIds);
    try {
      commandService.expireOrders(command);
    } catch (Exception e) {
      log.error("주문 만료 처리 중 오류 발생: {}", e.getMessage(), e);
    }
  }
}
