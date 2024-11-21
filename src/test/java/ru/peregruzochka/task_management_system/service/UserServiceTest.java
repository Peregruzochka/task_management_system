package ru.peregruzochka.task_management_system.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import ru.peregruzochka.task_management_system.entity.User;
import ru.peregruzochka.task_management_system.repository.UserRepository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private AdminService adminService;

    @InjectMocks
    private UserService userService;

    private User user;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .email("test@example.com")
                .encodedPassword("encoded-password")
                .build();
    }


    @Test
    void init_CallsCreateSuperAdminIfNotExists() {
        userService.init();
        verify(adminService, times(1)).createSuperAdminIfNotExists();
    }

    @Test
    void signUp_RegistersNewUser_WhenEmailNotExists() {
        when(userRepository.existsByEmail(user.getEmail())).thenReturn(false);
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        User result = userService.signUp(user);

        assertNotNull(result);
        assertEquals(user.getEmail(), result.getEmail());
        verify(userRepository, times(1)).existsByEmail(user.getEmail());
        verify(userRepository, times(1)).save(user);
    }

    @Test
    void signUp_ThrowsException_WhenEmailAlreadyExists() {
        when(userRepository.existsByEmail(user.getEmail())).thenReturn(true);

        assertThrows(IllegalArgumentException.class, () -> userService.signUp(user));
        verify(userRepository, times(1)).existsByEmail(user.getEmail());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void signIn_Success_WhenUserExistsAndPasswordMatches() {
        when(userRepository.findByEmail(user.getEmail())).thenReturn(java.util.Optional.of(user));
        when(passwordEncoder.matches(user.getEncodedPassword(), user.getEncodedPassword())).thenReturn(true);

        User result = userService.signIn(user);

        assertNotNull(result);
        assertEquals(user.getEmail(), result.getEmail());
        verify(userRepository, times(1)).findByEmail(user.getEmail());
    }

    @Test
    void signIn_ThrowsException_WhenUserNotFound() {
        when(userRepository.findByEmail(user.getEmail())).thenReturn(java.util.Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> userService.signIn(user));
        verify(userRepository, times(1)).findByEmail(user.getEmail());
    }

    @Test
    void signIn_ThrowsException_WhenPasswordDoesNotMatch() {
        User storedUser = User.builder()
                .email("test@example.com")
                .encodedPassword("wrong-encoded-password")
                .build();
        when(userRepository.findByEmail(user.getEmail())).thenReturn(java.util.Optional.of(storedUser));
        when(passwordEncoder.matches(user.getEncodedPassword(), storedUser.getEncodedPassword())).thenReturn(false);

        assertThrows(IllegalArgumentException.class, () -> userService.signIn(user));
        verify(userRepository, times(1)).findByEmail(user.getEmail());
    }
}
