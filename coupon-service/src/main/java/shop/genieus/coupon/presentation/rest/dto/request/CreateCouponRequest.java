package shop.genieus.coupon.presentation.rest.dto.request;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import shop.genieus.coupon.application.in.command.dto.CreateCouponCommand;
import shop.genieus.coupon.domain.model.vo.CouponDiscountRate;
import shop.genieus.coupon.domain.model.vo.CouponEndDate;
import shop.genieus.coupon.domain.model.vo.CouponExpiredDate;
import shop.genieus.coupon.domain.model.vo.CouponStartDate;

public record CreateCouponRequest(
    @NotBlank(message = "쿠퐁 이름은 필수입니다.") String couponName,
    @NotNull @Min(1) @Max(99) Integer couponDiscountRate,
    @NotNull @Min(1) Integer couponQuantity,
    @NotNull @FutureOrPresent(message = "쿠폰 발급 시작일은 오늘 이후여야 합니다.") LocalDateTime couponStartDate,
    @NotNull LocalDateTime couponEndDate,
    @NotNull LocalDateTime couponExpiredDate,
    Integer couponMaxPrice) {
  public CreateCouponCommand toCommand() {
    return new CreateCouponCommand(
        couponName,
        new CouponDiscountRate(couponDiscountRate),
        couponQuantity,
        new CouponStartDate(couponStartDate),
        new CouponEndDate(couponEndDate),
        new CouponExpiredDate(couponExpiredDate),
        couponMaxPrice);
  }
}
