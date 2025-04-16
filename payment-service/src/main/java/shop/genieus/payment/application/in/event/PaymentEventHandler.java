package shop.genieus.payment.application.in.event;

public interface PaymentEventHandler {

    void handle(Long orderId);
}
