package ru.peregruzochka.task_management_system.dto;


import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString
@Schema(description = "Request object for user sign-in")
public class SingInRequest {

    @NotBlank(message = "Email cannot be empty")
    @Email(message = "Invalid email format")
    @Schema(description = "User's email address", example = "user@example.com")
    private String email;

    @Pattern(regexp = "^.*(?=.{8,})(?=.*[a-zA-Z])(?=.*\\d).*$", message = "The password must contain at least 8 characters, including at least one letter and one number")
    @Schema(description = "User's password (minimum 8 characters, must include at least one letter and one number)", example = "Password123")
    private String password;
}
