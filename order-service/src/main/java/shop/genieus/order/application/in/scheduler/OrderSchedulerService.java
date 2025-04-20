package shop.genieus.order.application.in.scheduler;

import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import shop.genieus.order.application.in.command.OrderCommandService;
import shop.genieus.order.application.in.command.dto.ExpireOrderCommand;
import shop.genieus.order.application.out.persistence.OrderDelayQueuePort;
import shop.genieus.order.application.out.util.OrderTimePort;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class OrderSchedulerService {
  private final OrderTimePort timePort;
  private final OrderDelayQueuePort delayQueuePort;
  private final OrderCommandService commandService;

  public void popExpiredOrders() {
    long epochSecond = timePort.getEpochSecond();
    List<Long> expiredIds = delayQueuePort.popExpiredOrders(epochSecond);
    if (expiredIds.isEmpty()) {
      return;
    }
    ExpireOrderCommand command = ExpireOrderCommand.of(expiredIds);
    commandService.expireOrders(command);
  }
}
