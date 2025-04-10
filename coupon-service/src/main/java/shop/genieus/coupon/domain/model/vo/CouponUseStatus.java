package shop.genieus.coupon.domain.model.vo;

import lombok.Getter;

@Getter
public enum CouponUseStatus {
  AVAILABLE("사용가능"),
  USED("사용완료"),
  EXPIRED("기간만료"),
  CANCELLED("철회완료");

  private final String description; // enum은 불변이기 때문에 내부 필드도 불변이면 안정성 향상

  CouponUseStatus(String description) {
    this.description = description;
  }
}
