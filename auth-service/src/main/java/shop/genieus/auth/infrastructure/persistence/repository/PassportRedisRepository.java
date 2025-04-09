package shop.genieus.auth.infrastructure.persistence.repository;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;
import shop.genieus.auth.domain.model.Passport;
import shop.genieus.auth.infrastructure.persistence.util.LuaScriptProvider;

@Repository
@RequiredArgsConstructor
public class PassportRedisRepository {
  private static final String PASSPORT_PREFIX = "passport:";
  private static final String USER_PASSPORT_PREFIX = "user:passport:";

  private final StringRedisTemplate redisTemplate;
  private final ObjectMapper objectMapper;

  public Passport savePassport(Passport passport) {
    try {
      String passportKey = PASSPORT_PREFIX + passport.getSessionId();
      String userPassportKey = USER_PASSPORT_PREFIX + passport.getUserId();

      List<String> keys = Arrays.asList(passportKey, userPassportKey);

      String passportJson = objectMapper.writeValueAsString(passport);
      long ttlMillis = calculateTtlMillis(passport.getExpiresAt());

      redisTemplate.execute(
          LuaScriptProvider.getSavePassportScript(),
          keys,
          passportJson,
          String.valueOf(ttlMillis),
          passport.getSessionId());

      return passport;
    } catch (JsonProcessingException e) {
      throw new IllegalArgumentException(
          "Passport 캐싱 실패- passport: %s, message: %s"
              .formatted(passport.toString(), e.getMessage()));
    }
  }

  public Passport findPassportByUserId(Long userId) {
    String userPassportKey = USER_PASSPORT_PREFIX + userId;
    List<String> keys = Arrays.asList(userPassportKey, PASSPORT_PREFIX);

    String passportJson =
        redisTemplate.execute(LuaScriptProvider.getFindPassportByUserIdScript(), keys);
    if (passportJson == null) return null;

    try {
      return objectMapper.readValue(passportJson, Passport.class);
    } catch (JsonProcessingException e) {
      throw new IllegalArgumentException(
          "Passport 파싱 실패- userId: %s, message: %s".formatted(userId, e.getMessage()));
    }
  }

  private long calculateTtlMillis(LocalDateTime expiresAt) {
    long expiresAtMillis = expiresAt.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
    long nowMillis = System.currentTimeMillis();
    return Math.max(0, expiresAtMillis - nowMillis);
  }
}
