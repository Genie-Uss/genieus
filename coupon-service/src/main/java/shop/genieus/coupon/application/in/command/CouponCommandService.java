package shop.genieus.coupon.application.in.command;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import shop.genieus.coupon.application.in.command.dto.CreateCouponCommand;
import shop.genieus.coupon.application.out.persistence.CouponCommandPort;
import shop.genieus.coupon.domain.model.entity.Coupon;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class CouponCommandService {

  private final CouponCommandPort persistencePort;

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
    return persistencePort.createCoupon(coupon);
  }
}
