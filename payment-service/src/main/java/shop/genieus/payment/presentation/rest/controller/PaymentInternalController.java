package shop.genieus.payment.presentation.rest.controller;

import com.genieus.common.auth.annotation.WithPassport;
import com.genieus.common.auth.model.Passport;
import com.genieus.common.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import shop.genieus.payment.application.in.command.PaymentCommandService;
import shop.genieus.payment.presentation.rest.dto.request.CreatePaymentRequest;

@RestController
@RequiredArgsConstructor
@RequestMapping("/internal/v1/payments")
public class PaymentInternalController {

  private final PaymentCommandService paymentCommandService;

  @PostMapping
  ResponseEntity<ApiResponse<HttpStatusCode>> createPayment(
      @WithPassport Passport passport,
      @RequestBody CreatePaymentRequest createPaymentRequest
  ) {
    paymentCommandService.create(CreatePaymentRequest.toCommand(createPaymentRequest, passport.getUserId()));

    return ResponseEntity.ok(ApiResponse.ok(HttpStatus.CREATED));
  }
}
