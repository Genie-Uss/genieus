package shop.genieus.coupon.application.in.command.dto;

import java.time.LocalDateTime;
import shop.genieus.coupon.domain.model.vo.CouponUseStatus;

public record IssueCouponCommand(
    Long userId,
    Long couponId,
    LocalDateTime issuedDate,
    LocalDateTime expiredDate,
    CouponUseStatus status) {}
