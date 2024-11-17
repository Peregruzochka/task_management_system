package ru.peregruzochka.task_management_system.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.peregruzochka.task_management_system.dto.JwtAuthResponse;
import ru.peregruzochka.task_management_system.dto.SingUpRequest;

import ru.peregruzochka.task_management_system.entity.User;
import ru.peregruzochka.task_management_system.mapper.UserMapper;
import ru.peregruzochka.task_management_system.service.UserService;
import ru.peregruzochka.task_management_system.util.JwtTokenProvider;

@Slf4j
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
    private final UserMapper userMapper;
    private final UserService userService;
    private final JwtTokenProvider jwtTokenProvider;

    @PostMapping("/sing-up")
    @ResponseStatus(HttpStatus.CREATED)
    public JwtAuthResponse singUp(@RequestBody @Valid SingUpRequest singUpRequest) {
        User newUser = userMapper.toUserEntity(singUpRequest);
        User savedUser = userService.createUser(newUser);
        String token = jwtTokenProvider.generateToken(savedUser);
        return new JwtAuthResponse(token);
    }
}
