package ru.peregruzochka.task_management_system.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.peregruzochka.task_management_system.entity.UserRole;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Data Transfer Object representing a user")
public class UserDto {

    @Schema(description = "Unique identifier of the user", example = "c29b5c59-8ed3-44a6-907a-9bdb4c5721f5")
    private UUID id;

    @Schema(description = "Username of the user", example = "john_doe")
    private String username;

    @Schema(description = "User's email address", example = "user@example.com")
    @Email(message = "Email should be valid")
    private String email;

    @Schema(description = "Role assigned to the user", example = "ADMIN")
    private UserRole role;

    @Schema(description = "Timestamp when the user was created", example = "2023-11-01T10:15:30")
    private LocalDateTime createdAt;
}
