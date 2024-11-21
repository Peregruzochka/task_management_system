package ru.peregruzochka.task_management_system.mapper;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import ru.peregruzochka.task_management_system.dto.SingInRequest;
import ru.peregruzochka.task_management_system.dto.SingUpRequest;
import ru.peregruzochka.task_management_system.dto.UserDto;
import ru.peregruzochka.task_management_system.entity.User;
import ru.peregruzochka.task_management_system.entity.UserRole;

import static ru.peregruzochka.task_management_system.entity.UserRole.USER;

@Component
@RequiredArgsConstructor
public class UserMapper {

    private final PasswordEncoder passwordEncoder;

    public User toUserEntity(SingUpRequest singUpRequest) {
        return User.builder()
                .email(singUpRequest.getEmail())
                .encodedPassword(encode(singUpRequest.getPassword()))
                .username(singUpRequest.getUsername())
                .role(USER)
                .build();
    }

    public User toUserEntity(SingInRequest singInRequest) {
        return User.builder()
                .email(singInRequest.getEmail())
                .encodedPassword(singInRequest.getPassword())
                .build();
    }

    public UserDto toUserDto(User user) {
        return UserDto.builder()
                .id(user.getId())
                .email(user.getEmail())
                .username(user.getUsername())
                .role(user.getRole())
                .createdAt(user.getCreatedAt())
                .build();
    }

    private String encode(String password) {
        return passwordEncoder.encode(password);
    }
}
