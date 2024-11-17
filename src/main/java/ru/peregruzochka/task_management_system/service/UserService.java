package ru.peregruzochka.task_management_system.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.peregruzochka.task_management_system.entity.User;
import ru.peregruzochka.task_management_system.exception.EmailNotFoundException;
import ru.peregruzochka.task_management_system.mapper.UserDetailsMapper;
import ru.peregruzochka.task_management_system.repository.UserRepository;

@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {
    private final UserRepository userRepository;
    private final UserDetailsMapper userDetailsMapper;


    @Transactional
    public User createUser(User user) {
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new IllegalArgumentException("Email address already in use");
        }
        return userRepository.save(user);
    }

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String email) throws EmailNotFoundException {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new EmailNotFoundException(email));
        return userDetailsMapper.toUserDetails(user);
    }
}
