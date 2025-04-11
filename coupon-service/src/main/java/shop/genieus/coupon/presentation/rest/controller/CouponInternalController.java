package shop.genieus.coupon.presentation.rest.controller;

import com.genieus.common.internal.request.UseCouponRequest;
import com.genieus.common.internal.response.CouponClientResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import shop.genieus.coupon.application.in.command.CouponCommandService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/internal/v1/coupons")
public class CouponInternalController {

  private final CouponCommandService couponCommandService;

  // 쿠폰 사용
  @PostMapping("/usage")
  public CouponClientResponse useCoupon(@RequestBody UseCouponRequest request) {
    return couponCommandService.useCoupon(request);
  }
}
