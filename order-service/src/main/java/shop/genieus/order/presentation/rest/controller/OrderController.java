package shop.genieus.order.presentation.rest.controller;

import com.genieus.common.auth.annotation.WithPassport;
import com.genieus.common.auth.model.Passport;
import com.genieus.common.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import shop.genieus.order.application.in.command.OrderCommandService;
import shop.genieus.order.domain.model.entity.Order;
import shop.genieus.order.presentation.rest.dto.request.CreateOrderRequest;
import shop.genieus.order.presentation.rest.dto.request.PaymentRequest;
import shop.genieus.order.presentation.rest.dto.response.CreateOrderResponse;
import shop.genieus.order.presentation.rest.dto.response.PaymentResponse;

@Slf4j
@RestController
@RequestMapping("/api/v1/orders")
@RequiredArgsConstructor
public class OrderController {
  private final OrderCommandService orderCommandService;

  @PostMapping
  public ResponseEntity<ApiResponse<CreateOrderResponse>> createOrder(
      @WithPassport Passport passport, @Valid @RequestBody CreateOrderRequest request) {
    log.info("Create order request: {}", request);
    Order order = orderCommandService.create(request.toCommand(passport));
    CreateOrderResponse response = CreateOrderResponse.toResponse(order);
    return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.created(response));
  }

  @PostMapping("/{orderId}/payment")
  public ResponseEntity<ApiResponse<PaymentResponse>> processPayment(
      @WithPassport Passport passport,
      @PathVariable Long orderId,
      @Valid @RequestBody PaymentRequest request) {
    log.info("Process payment request: {}", request);
    Order order = orderCommandService.payment(request.toCommand(passport, orderId));
    PaymentResponse response = PaymentResponse.toResponse(order);
    return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.ok(response));
  }
}
