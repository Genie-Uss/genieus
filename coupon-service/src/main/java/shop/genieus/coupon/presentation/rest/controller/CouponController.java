package shop.genieus.coupon.presentation.rest.controller;

import com.genieus.common.auth.annotation.WithPassport;
import com.genieus.common.auth.model.Passport;
import com.genieus.common.response.ApiResponse;
import java.net.URI;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import shop.genieus.coupon.application.in.command.CouponCommandService;
import shop.genieus.coupon.presentation.rest.dto.request.CreateCouponRequest;
import shop.genieus.coupon.presentation.rest.dto.response.CouponResponse;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/coupons")
public class CouponController {

  private final CouponCommandService couponCommandService;

  @PostMapping
  public ResponseEntity<ApiResponse<CouponResponse>> createCoupon(
      @RequestBody @Validated CreateCouponRequest request) {
    CouponResponse response =
        CouponResponse.from(couponCommandService.createCoupon(request.toCommand()));
    URI uri = generateUri(response.getCouponId());
    return ResponseEntity.created(uri).body(ApiResponse.created(response));
  }

  @PostMapping("/{couponId}")
  public ResponseEntity<ApiResponse<String>> issueCoupon(
      @WithPassport Passport passport, @PathVariable Long couponId) {
    couponCommandService.issueCoupon(couponId, passport.getUserId());
    return ResponseEntity.ok(ApiResponse.ok("쿠폰이 정상적으로 발급되었습니다."));
  }

  private URI generateUri(Long id) {
    return ServletUriComponentsBuilder.fromCurrentRequest()
        .path("/{id}")
        .buildAndExpand(id)
        .toUri();
  }
}
