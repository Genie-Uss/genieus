package shop.genieus.payment.presentation.rest.controller;

import com.genieus.common.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;
import shop.genieus.payment.application.in.command.PaymentCommandService;
import shop.genieus.payment.application.out.strategy.PaymentProcessorResult;
import shop.genieus.payment.presentation.rest.dto.request.CreatePaymentRequest;
import shop.genieus.payment.presentation.rest.dto.request.ProcessPaymentRequest;
import shop.genieus.payment.presentation.rest.dto.request.RegisterPaymentRequest;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/payments")
public class PaymentController {

    private final PaymentCommandService paymentCommandService;

    // TEST 용 API
    @PostMapping
    ResponseEntity<ApiResponse<HttpStatusCode>> createPayment(
            @RequestBody CreatePaymentRequest createPaymentRequest
    ) {
        paymentCommandService.create(CreatePaymentRequest.toCommand(createPaymentRequest, 1L));

        return ResponseEntity.ok(ApiResponse.ok(HttpStatus.CREATED));
    }

    @PostMapping("/process")
    Object processPayment(@RequestBody ProcessPaymentRequest processPaymentRequest) {
        PaymentProcessorResult paymentProcessorResult =
                paymentCommandService.processPayment(ProcessPaymentRequest.toCommand(processPaymentRequest));

        return switch (paymentProcessorResult.resultType()) {
            case JSON -> ResponseEntity.ok(ApiResponse.ok(paymentProcessorResult.payload()));
            case REDIRECT -> new RedirectView(paymentProcessorResult.payload().toString());
        };
    }

    @PostMapping("/success")
    ResponseEntity<ApiResponse<HttpStatusCode>> registerPaymentSuccess(
            @RequestBody RegisterPaymentRequest registerPaymentRequest
    ) {
        paymentCommandService.registerPaymentSuccess(RegisterPaymentRequest.toCommand(registerPaymentRequest));

        return ResponseEntity.ok(ApiResponse.ok(HttpStatus.CREATED));
    }

    @PostMapping("/test/{orderId}")
    ResponseEntity<ApiResponse<HttpStatusCode>> registerPaymentSuccess(
            @PathVariable Long orderId
    ) {
        paymentCommandService.registerPaymentSuccessForTest(orderId);

        return ResponseEntity.ok(ApiResponse.ok(HttpStatus.CREATED));
    }
}
