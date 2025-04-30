package shop.genieus.payment.global.event;

import java.util.List;

public interface PaymentEvent {

  List<Object> getContext();
}
