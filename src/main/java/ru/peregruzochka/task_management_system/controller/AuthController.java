package ru.peregruzochka.task_management_system.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.peregruzochka.task_management_system.dto.SingInRequest;
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
    public ResponseEntity<?> singUp(@RequestBody @Valid SingUpRequest singUpRequest) {
        User newUser = userMapper.toUserEntity(singUpRequest);
        User savedUser = userService.signUp(newUser);
        String token = jwtTokenProvider.generateToken(savedUser);

        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + token);
        return new ResponseEntity<>(headers, HttpStatus.CREATED);
    }

    @PostMapping("/sing-in")
    public ResponseEntity<?> singIn(@RequestBody @Valid SingInRequest singInRequest) {
        User user = userMapper.toUserEntity(singInRequest);
        User savedUser = userService.signIn(user);
        String token = jwtTokenProvider.generateToken(savedUser);

        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + token);
        return new ResponseEntity<>(headers, HttpStatus.OK);
    }
}
