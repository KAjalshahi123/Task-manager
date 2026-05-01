package com.taskmanager.security;

import com.taskmanager.entity.User;
import com.taskmanager.enums.Role;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;

@Data
@AllArgsConstructor
public class UserDetailsImpl implements UserDetails {

    private Long id;
    private String email;
    private String password;
    private String firstName;
    private String lastName;
    private String username;
    private Role role;
    private boolean active;

    public static UserDetailsImpl build(User user) {
        return new UserDetailsImpl(
                user.getId(),
                user.getEmail(),
                user.getPassword(),
                user.getFirstName(),
                user.getLastName(),
                user.getUsername(),
                user.getRole(),
                user.isActive()
        );
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singletonList(
                new SimpleGrantedAuthority("ROLE_" + role.name())
        );
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return active;
    }
}
