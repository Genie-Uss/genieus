package shop.genieus.auth.domain.model;

import com.genieus.common.auth.model.RoleType;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Passport {
  public static final int DEFAULT_SESSION_HOURS = 24;

  private String sessionId;
  private Long userId;
  private RoleType role;
  private LocalDateTime issuedAt;
  private LocalDateTime expiresAt;

  private static Passport create(
      String sessionId, Long userId, RoleType role, LocalDateTime now, int sessionHours) {
    validatePassport(sessionId, userId, role, now);

    return Passport.builder()
        .sessionId(sessionId)
        .userId(userId)
        .role(role)
        .issuedAt(now)
        .expiresAt(now.plusHours(sessionHours))
        .build();
  }

  public static Passport create(String sessionId, Long userId, RoleType role, LocalDateTime now) {
    return create(sessionId, userId, role, now, DEFAULT_SESSION_HOURS);
  }

  private static void validatePassport(
      String sessionId, Long userId, RoleType role, LocalDateTime issuedAt) {
    if (sessionId == null || sessionId.isBlank()) {
      throw new IllegalArgumentException("세션 ID는 빈 값일 수 없습니다.");
    }
    if (userId == null || userId <= 0) {
      throw new IllegalArgumentException("유효하지 않은 사용자 ID입니다.");
    }
    if (role == null) {
      throw new IllegalArgumentException("역할 정보는 필수입니다.");
    }
    if (issuedAt == null) {
      throw new IllegalArgumentException("발급 시간은 필수입니다.");
    }
  }

  public Passport extend(LocalDateTime now, int hours) {
    if (now == null) {
      throw new IllegalArgumentException("현재 시간은 필수입니다.");
    }
    if (hours <= 0) {
      throw new IllegalArgumentException("연장 시간은 0보다 커야 합니다.");
    }

    return Passport.builder()
        .sessionId(this.sessionId)
        .userId(this.userId)
        .role(this.role)
        .issuedAt(this.issuedAt)
        .expiresAt(now.plusHours(hours))
        .build();
  }
}
