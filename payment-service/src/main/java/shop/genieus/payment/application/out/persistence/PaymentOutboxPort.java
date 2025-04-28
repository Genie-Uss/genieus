package shop.genieus.payment.application.out.persistence;

import java.util.List;
import shop.genieus.payment.global.event.PaymentEvent;

public interface PaymentOutboxPort {

  void save(PaymentEvent paymentEvent);

  List<?> findEventsNotPublished();
}
