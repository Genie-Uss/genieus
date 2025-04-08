package shop.genieus.auth.domain.model;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import shop.genieus.auth.domain.model.vo.TokenId;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class TokenValidationResult {
  private final TokenId tokenId;
  private Long userId;

  public static TokenValidationResult create(String tokenIdValue, Long userId) {
    if (userId == null) {
      throw new IllegalArgumentException("사용자 ID는 필수입니다.");
    }
    return new TokenValidationResult(TokenId.of(tokenIdValue), userId);
  }
}
