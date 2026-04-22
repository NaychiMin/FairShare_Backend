package com.example.fairsharebackend.service;

import com.example.fairsharebackend.entity.*;
import com.example.fairsharebackend.repository.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserAuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserAuthService service;

    @Test
    @DisplayName("Load user by username successfully")
    void loadUserByUsername_success() {
        // ARRANGE
        String email = "test@test.com";

        User user = new User();
        user.setEmail(email);

        UserCredential cred = new UserCredential();
        cred.setPasswordHash("password");
        user.setUserCredential(cred);

        StaticRole role = new StaticRole();
        role.setName("ROLE_USER");

        user.setStaticRoles(Set.of(role));

        when(userRepository.findByEmail(email))
                .thenReturn(Optional.of(user));

        // ACT
        UserDetails details = service.loadUserByUsername(email);

        // ASSERT
        assertThat(details.getUsername()).isEqualTo(email);
        assertThat(details.getAuthorities()).isNotEmpty();
    }

    @Test
    @DisplayName("Throw exception when user not found")
    void loadUserByUsername_shouldThrow() {
        // ARRANGE
        when(userRepository.findByEmail(any()))
                .thenReturn(Optional.empty());

        // ACT & ASSERT
        assertThatThrownBy(() -> service.loadUserByUsername("x"))
                .isInstanceOf(UsernameNotFoundException.class);
    }
}