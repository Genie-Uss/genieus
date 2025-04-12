package shop.genieus.payment.application.in.command;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import shop.genieus.payment.application.dto.CreatePaymentCommand;
import shop.genieus.payment.application.dto.ProcessPaymentCommand;
import shop.genieus.payment.application.out.persistence.PaymentCommandPort;
import shop.genieus.payment.application.out.strategy.PaymentProcessorResult;
import shop.genieus.payment.application.out.strategy.PaymentStrategy;
import shop.genieus.payment.application.out.strategy.PaymentStrategyFactory;
import shop.genieus.payment.domain.model.entity.Payment;
import shop.genieus.payment.domain.model.vo.PaymentStatus;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentCommandService {

    private final PaymentCommandPort paymentCommandPort;
    private final PaymentStrategyFactory paymentStrategyFactory;

    @Transactional
    public Payment create(CreatePaymentCommand createPaymentCommand) {
        Payment payment = Payment.create(createPaymentCommand.toAssembler());
        return paymentCommandPort.create(payment);
    }

    @Transactional
    public PaymentProcessorResult processPayment(ProcessPaymentCommand processPaymentCommand) {
        Payment payment = findPaymentByOrderId(processPaymentCommand.orderId());
        payment.setPaymentMethod(processPaymentCommand.paymentMethod());

        checkPaymentStatus(payment.getPaymentStatus());

        PaymentStrategy paymentStrategy = paymentStrategyFactory.getStrategy(payment.getPaymentMethod());
        return paymentStrategy.process(payment);
    }

    private Payment findPaymentByOrderId(Long orderId) {
        return paymentCommandPort.findPaymentByOrderId(orderId);
    }

    private void checkPaymentStatus(PaymentStatus paymentStatus) {
        switch (paymentStatus) {
            case SUCCESS -> throw new IllegalArgumentException("이미 결제 완료된 주문입니다.");
            case REFUNDED -> throw new IllegalArgumentException("이미 환불된 주문입니다.");
        }
    }
}
