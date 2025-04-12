package shop.genieus.payment.application.in.command;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import shop.genieus.payment.application.dto.CreatePaymentCommand;
import shop.genieus.payment.application.out.persistence.PaymentCommandPort;
import shop.genieus.payment.domain.model.entity.Payment;

@Service
@RequiredArgsConstructor
public class PaymentCommandService {

    private final PaymentCommandPort paymentCommandPort;

    @Transactional
    // void 여도 문제 없을듯
    public Payment create(CreatePaymentCommand createPaymentCommand) {
        Payment payment = Payment.create(createPaymentCommand.toAssembler());
        return paymentCommandPort.create(payment);
    }
}
