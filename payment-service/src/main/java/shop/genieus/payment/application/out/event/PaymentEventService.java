package shop.genieus.payment.application.out.event;

public interface PaymentEventService {

    void createPaymentEvent(Long orderId);
}
