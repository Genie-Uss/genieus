package shop.genieus.auth.application.in.command.dto;

import shop.genieus.auth.domain.model.TokenValidationResult;
import shop.genieus.auth.domain.model.vo.TokenId;

public record IssuePassportCommand(TokenId tokenId, Long userId) {
  public static IssuePassportCommand from(TokenValidationResult tokenValidationResult) {
    return new IssuePassportCommand(
        tokenValidationResult.getTokenId(), tokenValidationResult.getUserId());
  }
}
