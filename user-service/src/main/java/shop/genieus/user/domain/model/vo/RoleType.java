package shop.genieus.user.domain.model.vo;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum RoleType {
  MASTER_ADMIN("시스템 전체 관리자", "시스템의 모든 기능에 접근 가능한 최고 관리자"),
  SHOP_ADMIN("상점 관리자", "특정 상점의 상품, 주문, 고객 관리 권한을 가진 관리자"),
  CUSTOMER("일반 고객", "상품을 구매할 수 있는 일반 사용자"),
  ;
  private final String title;
  private final String description;
}
