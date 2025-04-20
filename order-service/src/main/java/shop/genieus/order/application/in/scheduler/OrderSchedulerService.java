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
    log.debug("만료된 주문 조회 시작");
    long epochSecond = timePort.getEpochSecond();
    List<Long> expiredIds;
    try {
      expiredIds = delayQueuePort.popExpiredOrders(epochSecond);
    } catch (Exception e) {
      log.error("만료된 주문 조회 중 오류 발생: {}", e.getMessage(), e);
      return;
    }
    if (expiredIds.isEmpty()) {
      log.debug("만료된 주문 없음");
      return;
    }
    log.info("만료된 주문 처리 실행: {} 건", expiredIds.size());
    ExpireOrderCommand command = ExpireOrderCommand.of(expiredIds);
    try {
      commandService.expireOrders(command);
      log.info("만료된 주문 처리 완료: {}", expiredIds);
    } catch (Exception e) {
      log.error("주문 만료 처리 중 오류 발생: {}", e.getMessage(), e);
    }
  }
}
