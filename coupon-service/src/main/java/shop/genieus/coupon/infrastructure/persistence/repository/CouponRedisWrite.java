package shop.genieus.coupon.infrastructure.persistence.repository;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.stereotype.Component;
import shop.genieus.coupon.domain.model.entity.CouponUser;
import shop.genieus.coupon.infrastructure.persistence.dto.IssueCouponCommand;

@Component
@RequiredArgsConstructor
public class CouponRedisWrite implements ItemWriter<IssueCouponCommand> {
  private final CouponUserJpaRepository couponUserJpaRepository;

  @Override
  public void write(Chunk<? extends IssueCouponCommand> chunk) throws Exception {
    if (chunk == null || chunk.isEmpty()) return;

    List<CouponUser> entities =
        chunk.getItems().stream().map(dto -> CouponUser.create(dto)).toList();

    couponUserJpaRepository.bulkInsert(entities);
  }
}
