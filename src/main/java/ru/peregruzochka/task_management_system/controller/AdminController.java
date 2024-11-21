package ru.peregruzochka.task_management_system.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.peregruzochka.task_management_system.dto.SingUpRequest;
import ru.peregruzochka.task_management_system.dto.UserDto;
import ru.peregruzochka.task_management_system.entity.User;
import ru.peregruzochka.task_management_system.mapper.UserMapper;
import ru.peregruzochka.task_management_system.service.AdminService;

import java.util.UUID;

@RestController
@RequestMapping("/admins")
@RequiredArgsConstructor
public class AdminController {
    private final AdminService adminService;
    private final UserMapper userMapper;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserDto singUpAdmin(@RequestBody @Valid SingUpRequest request) {
        User admin = userMapper.toUserEntity(request);
        User savedAdmin = adminService.createAdmin(admin);
        return userMapper.toUserDto(savedAdmin);
    }

    @PutMapping("/promotion")
    @ResponseStatus(HttpStatus.OK)
    public UserDto promoteToAdmin(@RequestParam("user-id") UUID userId) {
        User newAdmin = adminService.promoteToAdmin(userId);
        return userMapper.toUserDto(newAdmin);
    }

    @PutMapping("/demotion")
    @ResponseStatus(HttpStatus.OK)
    public UserDto demoteFromAdmin(@RequestParam("user-id") UUID userId) {
        User newAdmin = adminService.demoteFromAdmin(userId);
        return userMapper.toUserDto(newAdmin);
    }
}
