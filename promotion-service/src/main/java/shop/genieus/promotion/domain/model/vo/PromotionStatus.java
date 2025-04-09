package shop.genieus.promotion.domain.model.vo;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum PromotionStatus {

  READY("프로모션 대기"),
  ON_PROMOTION("프로모션 진행중"),
  END("프로모션 종료");

  private final String description;
}
