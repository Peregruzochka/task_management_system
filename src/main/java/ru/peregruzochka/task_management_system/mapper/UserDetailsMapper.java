package ru.peregruzochka.task_management_system.mapper;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import ru.peregruzochka.task_management_system.entity.User;

import java.util.Collection;
import java.util.List;

@Component
public class UserDetailsMapper {
    public UserDetails toUserDetails(User user) {
        return new UserDetails() {
            @Override
            public Collection<? extends GrantedAuthority> getAuthorities() {
                GrantedAuthority authority = new SimpleGrantedAuthority(user.getRole().name());
                return List.of(authority);
            }

            @Override
            public String getPassword() {
                return user.getEncodedPassword();
            }

            @Override
            public String getUsername() {
                return user.getEmail();
            }
        };
    }
}
