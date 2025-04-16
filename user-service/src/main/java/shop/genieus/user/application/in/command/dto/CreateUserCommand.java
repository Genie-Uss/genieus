package shop.genieus.user.application.in.command.dto;

public record CreateUserCommand(
    String email,
    String name,
    String password,
    String confirmedPassword,
    String birthInfo,
    String phoneNumber,
    String address) {}
