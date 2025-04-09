package shop.genieus.auth.application.out.support.encoder;

import shop.genieus.auth.domain.model.Passport;

public interface PassportEncodingPort {
  String encode(Passport passport);
}
