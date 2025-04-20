package shop.genieus.order.application.in.command.dto;

import java.util.List;

public record ExpireOrderCommand(List<Long> orderIds) {
  public static ExpireOrderCommand of(List<Long> list) {
    return new ExpireOrderCommand(list);
  }
}
