package shop.genieus.auth.domain.model.vo;

public record JwtAssociation(TokenId tokenId, Long userId) {
  public JwtAssociation of(String tokenIdValue, Long userId) {
    TokenId tokenId = TokenId.of(tokenIdValue);
    validateUserId(userId);
    return new JwtAssociation(tokenId, userId);
  }

  private void validateUserId(Long userId) {
    if (userId == null) {
      throw new IllegalArgumentException("사용자 ID는 필수입니다.");
    }
  }
}
