package shop.genieus.auth.domain.model.vo;

public record TokenId(String value) {

  public TokenId {
    validateTokenId(value);
  }

  public static TokenId of(String value) {
    return new TokenId(value);
  }

  private static void validateTokenId(String value) {
    if (value == null || value.isBlank()) {
      throw new IllegalArgumentException("토큰 ID는 빈 값일 수 없습니다.");
    }
  }
}
