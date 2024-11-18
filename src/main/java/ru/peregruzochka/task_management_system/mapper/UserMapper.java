package ru.peregruzochka.task_management_system.mapper;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import ru.peregruzochka.task_management_system.dto.SingUpRequest;
import ru.peregruzochka.task_management_system.entity.User;
import ru.peregruzochka.task_management_system.entity.UserRole;

@Component
@RequiredArgsConstructor
public class UserMapper {

    private final PasswordEncoder passwordEncoder;

    public User toUserEntity(SingUpRequest singUpRequest) {
        return User.builder()
                .email(singUpRequest.getEmail())
                .encodedPassword(encode(singUpRequest.getPassword()))
                .username(singUpRequest.getUsername())
                .role(UserRole.ROLE_USER)
                .build();
    }

    private String encode(String password) {
        return passwordEncoder.encode(password);
    }
}
