package shop.genieus.coupon.application.in.command;

import com.genieus.common.internal.request.UseCouponRequest;
import com.genieus.common.internal.response.CouponClientResponse;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import shop.genieus.coupon.application.in.command.dto.CreateCouponCommand;
import shop.genieus.coupon.application.out.persistence.CouponCommandPort;
import shop.genieus.coupon.domain.model.entity.Coupon;
import shop.genieus.coupon.domain.model.entity.CouponUser;
import shop.genieus.coupon.domain.model.vo.CouponUseStatus;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class CouponCommandService {

  private final CouponCommandPort commandPort;

  public Coupon createCoupon(CreateCouponCommand request) {
    // todo. user 권한 검증 (MASTER 만 가능)
    // todo. Refactor) 발급 종료일자, 만료일자 검증 커스텀 어노테이션 생성
    if (!request.couponEndDate().validateEndDate(request.couponStartDate().getValue())) {
      throw new IllegalArgumentException("발급 종료일자는 시작일자보다 이전일 수 없습니다.");
    }
    if (!request.couponExpiredDate().validateExpiredDate(request.couponStartDate().getValue())) {
      throw new IllegalArgumentException("발급 만료일자는 시작일자보다 이전일 수 없습니다.");
    }
    Coupon coupon = CreateCouponCommand.toEntity(request);
    // DB 저장
    Coupon saved = commandPort.createCoupon(coupon);
    // Redis 저장
    commandPort.saveInitialStock(coupon);
    return saved;
  }

  public CouponClientResponse useCoupon(UseCouponRequest request) {
    // 1. 쿠폰 사용 가능 여부 확인
    CouponUser couponUser = findAvailableCoupon(request);
    // 쿠폰 정보 조회
    Coupon coupon = commandPort.findCoupon(request.couponId());
    // 2. 쿠폰 사용
    couponUser.useCoupon();
    return new CouponClientResponse(
        coupon.getCouponId(),
        coupon.getCouponDiscountRate().getValue(),
        coupon.getCouponMaxPrice());
  }

  public void issueCoupon(Long couponId, Long userId) {
    // 1. 쿠폰 유효성 검사 (쿠폰 발급 기간,
    Coupon coupon = commandPort.findCoupon(couponId);
    // 2. 쿠폰 재고 확인 및 발급
    // todo. lua script 원자성은 보장되지만 도중에 실패했을 때 롤백은 안되는 것 같다.
    commandPort.createCouponUser(coupon, userId);
  }

  private CouponUser findAvailableCoupon(UseCouponRequest request) {
    CouponUser couponUser = commandPort.validUserCoupon(request.couponId(), request.userId());
    if (couponUser == null) {
      throw new IllegalArgumentException("존재하지 않는 쿠폰입니다.");
    }
    if (!couponUser.getCouponUserStatus().equals(CouponUseStatus.AVAILABLE)) {
      throw new IllegalArgumentException("사용 불가능한 쿠폰입니다.");
    }
    if (couponUser.getCouponUserExpiredDate().isBefore(request.orderedAt())) {
      throw new IllegalArgumentException("사용 기한이 지난 쿠폰입니다.");
    }
    return couponUser;
  }
}
