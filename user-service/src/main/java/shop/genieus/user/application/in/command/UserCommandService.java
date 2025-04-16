package shop.genieus.user.application.in.command;

import com.genieus.common.auth.model.RoleType;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import shop.genieus.user.application.in.command.dto.CreateUserCommand;
import shop.genieus.user.application.out.persistence.UserCommandPort;
import shop.genieus.user.application.out.support.encoder.PasswordEncryptionPort;
import shop.genieus.user.domain.model.entity.User;
import shop.genieus.user.global.exception.ExistEmailException;
import shop.genieus.user.global.exception.PasswordMismatchException;
import shop.genieus.user.global.exception.UserException;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class UserCommandService {
  private final UserCommandPort commandPort;
  private final PasswordEncryptionPort encryptionPort;

  public User createUser(CreateUserCommand command) {
    if (!command.password().equals(command.confirmedPassword())) {
      throw new PasswordMismatchException();
    }

    if (commandPort.existsByEmail(command.email())) {
      throw new ExistEmailException();
    }

    try {
      User user =
          User.create(
              command.email(),
              command.name(),
              command.password(),
              encryptionPort,
              RoleType.CUSTOMER,
              command.birthInfo(),
              command.phoneNumber(),
              command.address());

      User savedUser = commandPort.save(user);
      log.info("회원가입 성공, user: {}", savedUser);

      return savedUser;
    } catch (Exception exception) {
      log.info("회원가입 중 오류: {}", exception.getMessage());

      int createdUserFailCode = 1101;
      throw new UserException(exception.getMessage(), createdUserFailCode);
    }
  }
}
