package ru.peregruzochka.task_management_system.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import ru.peregruzochka.task_management_system.dto.SingUpRequest;
import ru.peregruzochka.task_management_system.dto.UserDto;
import ru.peregruzochka.task_management_system.entity.User;
import ru.peregruzochka.task_management_system.mapper.UserMapper;
import ru.peregruzochka.task_management_system.service.AdminService;

import java.util.UUID;

@RestController
@RequestMapping("/admins")
@RequiredArgsConstructor
@Tag(
        name = "Admins",
        description = """
                Controller for managing admin roles.
                Allows super administrator to create new admins, promote users to admins, or demote admins to users.
                These operations are restricted to SUPER_ADMIN role.
                """
)
public class AdminController {

    private final AdminService adminService;
    private final UserMapper userMapper;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(
            summary = "Sign up a new admin. [Super admin only]",
            description = "Creates a new admin account. Requires valid user details in the request body.",
            security = @SecurityRequirement(name = "Bearer Authentication")
    )
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public UserDto singUpAdmin(@RequestBody @Valid SingUpRequest request) {
        User admin = userMapper.toUserEntity(request);
        User savedAdmin = adminService.createAdmin(admin);
        return userMapper.toUserDto(savedAdmin);
    }

    @PutMapping("/promotion")
    @ResponseStatus(HttpStatus.OK)
    @Operation(
            summary = "Promote a user to admin. [Super admin only]",
            description = "Promotes an existing user to admin role. Requires the user's ID as a request parameter.",
            security = @SecurityRequirement(name = "Bearer Authentication")
    )
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public UserDto promoteToAdmin(@RequestParam("user-id") UUID userId) {
        User newAdmin = adminService.promoteToAdmin(userId);
        return userMapper.toUserDto(newAdmin);
    }

    @PutMapping("/demotion")
    @ResponseStatus(HttpStatus.OK)
    @Operation(
            summary = "Demote an admin to user. [Super admin only]",
            description = "Demotes an existing admin to a regular user role. Requires the admin's ID as a request parameter.",
            security = @SecurityRequirement(name = "Bearer Authentication")
    )
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public UserDto demoteFromAdmin(@RequestParam("user-id") UUID userId) {
        User newAdmin = adminService.demoteFromAdmin(userId);
        return userMapper.toUserDto(newAdmin);
    }
}
