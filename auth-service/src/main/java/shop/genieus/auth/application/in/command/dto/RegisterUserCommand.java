package shop.genieus.auth.application.in.command.dto;

import com.genieus.common.auth.model.RoleType;

public record RegisterUserCommand(
    Long id, String email, String hashedPassword, RoleType roleType) {}
