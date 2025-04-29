package shop.genieus.payment.application.in.command;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import shop.genieus.payment.application.in.dto.CreatePaymentCommand;
import shop.genieus.payment.application.in.dto.ProcessPaymentCommand;
import shop.genieus.payment.application.in.dto.RegisterPaymentCommand;
import shop.genieus.payment.application.out.cache.PaymentCachePort;
import shop.genieus.payment.application.out.dto.CompletedPaymentResult;
import shop.genieus.payment.application.out.event.PaymentEventService;
import shop.genieus.payment.application.out.persistence.PaymentCommandPort;
import shop.genieus.payment.application.out.persistence.PaymentOutboxPort;
import shop.genieus.payment.application.out.strategy.PaymentProcessorResult;
import shop.genieus.payment.application.out.strategy.PaymentStrategy;
import shop.genieus.payment.application.out.strategy.PaymentStrategyFactory;
import shop.genieus.payment.domain.model.entity.Payment;
import shop.genieus.payment.domain.model.vo.PaymentStatus;
import shop.genieus.payment.global.exception.PaymentErrorCode;
import shop.genieus.payment.global.exception.PaymentException;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentCommandService {

  private final PaymentCommandPort paymentCommandPort;
  private final PaymentStrategyFactory paymentStrategyFactory;
  private final PaymentCachePort paymentCachePort;
  private final PaymentOutboxPort paymentOutboxPort;
  private final PaymentEventService paymentEventService;

  @Transactional
  public Payment create(CreatePaymentCommand createPaymentCommand) {
    try {
      Payment payment = Payment.create(createPaymentCommand.toAssembler());
      Payment savedPayment = paymentCommandPort.create(payment);

      return paymentCachePort.putPaymentCache(savedPayment);
    } catch (DataAccessException dae) {
      log.warn(dae.getMessage());
      throw new PaymentException(PaymentErrorCode.PAYMENT_DUPLICATED_ERROR, dae);
    } catch (RuntimeException e) {
      log.warn("[결제 요청 실패] 원인: {}", e.getMessage());
      throw new PaymentException(PaymentErrorCode.PAYMENT_REQUEST_FAILED, e);
    }
  }

  @Transactional
  public PaymentProcessorResult processPayment(ProcessPaymentCommand processPaymentCommand) {
    Payment payment = findPaymentByOrderId(processPaymentCommand.orderId());
    payment.setPaymentMethod(processPaymentCommand.paymentMethod());

    checkPaymentStatus(payment.getPaymentStatus());

    PaymentStrategy paymentStrategy = paymentStrategyFactory.getStrategy(payment.getPaymentMethod());
    return paymentStrategy.process(payment);
  }

  @Transactional
  public Payment registerPaymentSuccess(RegisterPaymentCommand registerPaymentCommand) {
    Payment payment = findPaymentByOrderId(registerPaymentCommand.orderId());
    payment.registerPaymentSuccess();

    publishPaymentSuccessEvent(payment);
    paymentCachePort.putPaymentCache(payment);

    return payment;
  }

  @Transactional
  public void registerPaymentSuccessForTest(Long orderId) {
    Payment payment = findPaymentByOrderId(orderId);
    payment.setPaymentSuccessForTest();

    publishPaymentSuccessEvent(payment);
    paymentCachePort.putPaymentCache(payment);
    paymentOutboxPort.save(CompletedPaymentResult.of(orderId));
  }

  @Transactional
  public Payment cancel(Long orderId) {
    Payment payment = findPaymentByOrderId(orderId);
    payment.cancel();

    paymentCachePort.removePaymentCache(orderId);

    return payment;
  }

  private Payment findPaymentByOrderId(Long orderId) {
    return paymentCommandPort.findPaymentByOrderId(orderId);
  }

  private void checkPaymentStatus(PaymentStatus paymentStatus) {
    switch (paymentStatus) {
      case SUCCESS -> throw new PaymentException(PaymentErrorCode.INVALID_PAYMENT_REGISTER_PAID);
      case REFUNDED -> throw new PaymentException(PaymentErrorCode.INVALID_PAYMENT_REGISTER_REFUNDED);
    }
  }

  private void publishPaymentSuccessEvent(Payment payment) {
    paymentEventService.createPaymentEvent(payment.getOrderId());
  }
}
