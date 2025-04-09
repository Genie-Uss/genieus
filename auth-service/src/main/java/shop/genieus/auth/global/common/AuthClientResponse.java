package shop.genieus.auth.global.common;

import com.genieus.common.passport.constant.PassportConstant;

public record AuthClientResponse(String encodedPassport, String passportHeaderKey) {
  public static AuthClientResponse from(String encodedPassport, String passportHeaderKey) {
    return new AuthClientResponse(encodedPassport, passportHeaderKey);
  }

  public static AuthClientResponse from(String encodedPassport) {
    return AuthClientResponse.from(encodedPassport, PassportConstant.PASSPORT_HEADER);
  }
}
