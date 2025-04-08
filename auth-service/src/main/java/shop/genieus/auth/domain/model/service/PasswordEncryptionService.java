package shop.genieus.auth.domain.model.service;

public interface PasswordEncryptionService {
  String encode(String rawPassword);

  boolean matches(String rawPassword, String encodedPassword);
}
