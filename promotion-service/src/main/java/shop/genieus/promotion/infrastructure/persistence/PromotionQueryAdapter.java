package shop.genieus.promotion.infrastructure.persistence;

import static shop.genieus.promotion.global.constants.Code.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import shop.genieus.promotion.application.in.query.dto.VerifyProductsRateQuery;
import shop.genieus.promotion.application.out.persistence.PromotionQueryPort;
import shop.genieus.promotion.domain.model.entity.PromotionProduct;
import shop.genieus.promotion.domain.model.vo.Product;
import shop.genieus.promotion.global.exception.PromotionException;
import shop.genieus.promotion.infrastructure.persistence.repository.PromotionRedisHashRepository;

@Slf4j
@Component
@RequiredArgsConstructor
public class PromotionQueryAdapter implements PromotionQueryPort {

  private static final String HASH_PREFIX = "promotion:time:";

  private final PromotionRedisHashRepository redisRepository;

  @Override
  public List<Product> verifyProductDiscountRate(VerifyProductsRateQuery query) {
    String hashKey = getKey(query.orderedAt());

    List<String> hashFields = makeHashFields(query); // productId:promotionId

    List<Integer> hashValues = redisRepository.getValues(hashKey, hashFields);

    return getProducts(hashValues, hashFields);
  }

  @Override
  public void saveProductDiscountRate(List<PromotionProduct> promotionProducts, LocalDateTime updatedAt) {
    /**
     * 6월 3일 ~ 6월 4일 00시 까지 판매, 6월 4일 1시에 스케줄 동작
     * 6월 4일 ~ 6월 4일 23시59분 59초까지 존재
     * 현재 날짜(4일) + 하루 날짜 기준 00시~23시59분59초 까지 제작
     *
     */
    if(promotionProducts == null || promotionProducts.isEmpty()) {
      log.error("프로모션 상품이 존재하지 않습니다. 갱신 요청 날짜 : {}, 조회 구간 : {}",
          updatedAt, updatedAt.plusDays(1L));
      return;
    }

    String nextHashKey = getNextHashKey(updatedAt);
    Map<String, Integer> rates = makeProductsData(promotionProducts);
    LocalDateTime dateTime = getDeleteTime(updatedAt);

    redisRepository.save(nextHashKey, rates, dateTime);
    log.info("Redis 최저가 갱신, HashKey : {}", nextHashKey);
  }

  @Override
  public void updateProductDiscountRate(String hashField, Integer discountRate) {
    Set<String> hashKeys = redisRepository.getHashKeys(HASH_PREFIX);

    for(String hashKey : hashKeys) {
      Map<String, Integer> map  = redisRepository.get(hashKey);
      map.put(hashField, discountRate);
      redisRepository.update(hashKey, map);
    }
  }

  private Map<String, Integer> makeProductsData(List<PromotionProduct> promotionProducts) {
    return promotionProducts.stream()
        .collect(Collectors.toMap(
            pp -> pp.getProductId() + ":" + pp.getPromotion().getPromotionId(),
            pp -> pp.getPromotionProductDiscountRate().getValue()
        ));
  }

  private LocalDateTime getDeleteTime(LocalDateTime updatedAt) {
    LocalDate nextDay = updatedAt.plusDays(1L).toLocalDate();
    LocalDateTime endDate = nextDay.atTime(23, 59, 59);
    return endDate.plusMinutes(30L);
  }

  private String getNextHashKey(LocalDateTime updatedAt) {
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH-mm-ss");

    LocalDate nextDay = updatedAt.plusDays(1).toLocalDate();
    LocalDateTime startDate = nextDay.atStartOfDay();
    LocalDateTime endDate = nextDay.atTime(23, 59, 59);
    return HASH_PREFIX + String.format(startDate.format(formatter))+ ":" + String.format(endDate.format(formatter));
  }

  private List<Product> getProducts(List<Integer> hashValues, List<String> hashFields) {
    List<Product> products = new ArrayList<>();
    for(int i=0; i< hashValues.size(); i++) {
      if(hashValues.get(i) == null) {
        throw new PromotionException(REDIS_RATE_NOT_FOUND);
      }
      String[] parts = hashFields.get(i).split(":");
      products.add(new Product(
          Long.parseLong(parts[0]),
          Long.parseLong(parts[1]),
          hashValues.get(i)
      ));
    }
    return products;
  }

  private List<String> makeHashFields(VerifyProductsRateQuery query) {
    return query.items()
        .stream()
        .map(item-> item.productId() + ":" + item.promotionId())
        .toList();
  }

  private String getKey(LocalDateTime orderedAt) {
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH-mm-ss");

    Set<String> keys = redisRepository.getHashKeys(HASH_PREFIX);

    for(String key : keys) {
      String[] parts = key.split(":");
      LocalDateTime startedAt = LocalDateTime.parse(parts[2], formatter);
      LocalDateTime endedAt = LocalDateTime.parse(parts[3], formatter);
      if(startedAt.isAfter(orderedAt) || endedAt.isBefore(orderedAt)) {
        continue;
      }
      return key;
    }
    throw new PromotionException(REDIS_DATE_NOT_FOUND);
  }
}
