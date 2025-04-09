package shop.genieus.coupon.domain.model.vo;

import lombok.Getter;

@Getter
public enum CouponStatus {
  READY("발급대기"),
  IN_PROGRESS("발급중"),
  PAUSED("발급중지"),
  ENDED("발급종료");

  private final String description;

  CouponStatus(String description) {
    this.description = description;
  }
}
