package ru.peregruzochka.task_management_system.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;


@Setter
@Getter
@ToString
public class SingUpRequest {
    @NotBlank(message = "Email cannot be empty")
    @Email(message = "Invalid email format")
    private String email;

    @Pattern(regexp =  "(?=.*[A-Za-z])(?=.*\\d).{8,}$", message = "The password must contain at least 8 characters, including at least one letter and one number")
    private String password;

    @NotBlank(message = "Username cannot be empty")
    private String username;
}
