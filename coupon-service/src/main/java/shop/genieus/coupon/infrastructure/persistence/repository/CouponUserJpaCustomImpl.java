package shop.genieus.coupon.infrastructure.persistence.repository;

import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import shop.genieus.coupon.domain.model.entity.CouponUser;

@Slf4j
@Repository
@RequiredArgsConstructor
public class CouponUserJpaCustomImpl implements CouponUserJpaCustom {

  private final EntityManager em;

  @Override
  @Transactional
  public void bulkInsert(List<CouponUser> entities) {
    if (entities.isEmpty()) return;

    StringBuilder sql = new StringBuilder();
    sql.append(
        "INSERT INTO m_coupon_user (user_id, coupon_id, coupon_user_issued_date, coupon_user_expired_date, coupon_user_status) VALUES");

    for (int i = 0; i < entities.size(); i++) {
      CouponUser couponUser = entities.get(i);
      sql.append(
          String.format(
              "(%d, %d, '%s', '%s', '%s')",
              couponUser.getUserId(),
              couponUser.getCoupon().getCouponId(),
              couponUser.getCouponUserIssuedDate(),
              couponUser.getCouponUserExpiredDate(),
              couponUser.getCouponUserStatus().name()));

      if (i != entities.size() - 1) {
        sql.append(",");
      }
    }
    int insertCount = em.createNativeQuery(sql.toString()).executeUpdate();
    log.info("쿠폰 배치 처리 Bulk insert : {} 개", insertCount);
  }
}
