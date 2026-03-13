package com.example.fairsharebackend.service;

import com.example.fairsharebackend.entity.User;
import com.example.fairsharebackend.entity.UserCredential;
import com.example.fairsharebackend.entity.dto.request.UserLoginRequestDto;
import com.example.fairsharebackend.entity.dto.request.UserRegisterRequestDto;
import com.example.fairsharebackend.entity.dto.request.UserUpdateRequestDto;
import com.example.fairsharebackend.entity.dto.response.UserLoginResponseDto;
import com.example.fairsharebackend.mapper.UserMapper;
import com.example.fairsharebackend.repository.UserRepository;
import com.example.fairsharebackend.security.JwtUtil;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserServiceImpl userService;

    private User user;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setUserId(UUID.randomUUID());
        user.setEmail("test@example.com");
        user.setName("Test User");
    }

    @Test
    @DisplayName("Get user by email successfully")
    void shouldGetUserByEmail() {
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));

        User result = userService.getUserByEmail("test@example.com");

        assertThat(result).isEqualTo(user);
    }

    @Test
    @DisplayName("Fail get user by email when not found")
    void shouldFailGetUserByEmailWhenNotFound() {
        when(userRepository.findByEmail("missing@example.com")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.getUserByEmail("missing@example.com"))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("User not found");
    }

    @Test
    @DisplayName("Login successfully")
    void shouldLoginSuccessfully() {
        UserLoginRequestDto request = new UserLoginRequestDto();
        request.setEmail(" Test@Example.com ");
        request.setPassword("rawPassword");

        Authentication authentication = mock(Authentication.class);

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));
        when(jwtUtil.generateToken(user)).thenReturn("jwt-token");

        UserLoginResponseDto response = userService.login(request);

        assertThat(response.getUser()).isEqualTo(user);
        assertThat(response.getJwt()).isEqualTo("jwt-token");
    }

    @Test
    @DisplayName("Fail login with invalid credentials")
    void shouldFailLoginWithInvalidCredentials() {
        UserLoginRequestDto request = new UserLoginRequestDto();
        request.setEmail("test@example.com");
        request.setPassword("wrongPassword");

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new BadCredentialsException("Bad credentials"));

        assertThatThrownBy(() -> userService.login(request))
                .isInstanceOf(BadCredentialsException.class)
                .hasMessageContaining("Invalid credentials");
    }

    @Test
    @DisplayName("Register user successfully")
    void shouldRegisterUserSuccessfully() {
        UserRegisterRequestDto dto = new UserRegisterRequestDto();
        dto.setEmail(" Test@Example.com ");
        dto.setPassword("password123");
        dto.setName("Test User");

        User mappedUser = new User();
        mappedUser.setEmail("test@example.com");

        when(userRepository.existsByEmail(" Test@Example.com ")).thenReturn(false);
        when(userMapper.toEntity(dto)).thenReturn(mappedUser);
        when(passwordEncoder.encode("password123")).thenReturn("hashed-password");
        when(userRepository.save(mappedUser)).thenReturn(mappedUser);

        User result = userService.registerUser(dto);

        assertThat(result).isEqualTo(mappedUser);
        assertThat(dto.getEmail()).isEqualTo("test@example.com");
        assertThat(mappedUser.getUserCredential()).isNotNull();
        assertThat(mappedUser.getUserCredential().getPasswordHash()).isEqualTo("hashed-password");
    }

    @Test
    @DisplayName("Fail register user when email already exists")
    void shouldFailRegisterUserWhenEmailExists() {
        UserRegisterRequestDto dto = new UserRegisterRequestDto();
        dto.setEmail("test@example.com");
        dto.setPassword("password123");

        when(userRepository.existsByEmail("test@example.com")).thenReturn(true);

        assertThatThrownBy(() -> userService.registerUser(dto))
                .isInstanceOf(BadCredentialsException.class)
                .hasMessageContaining("Email already in use");
    }

    @Test
    @DisplayName("Update user successfully")
    void shouldUpdateUserSuccessfully() {
        UUID userId = user.getUserId();

        UserUpdateRequestDto dto = new UserUpdateRequestDto();
        dto.setEmail(" Updated@Example.com ");
        dto.setName("Updated Name");

        when(userRepository.existsByEmailAndUserIdNot(" Updated@Example.com ", userId)).thenReturn(false);
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(userRepository.save(user)).thenReturn(user);

        User result = userService.updateUser(userId, dto);

        assertThat(result).isEqualTo(user);
        assertThat(dto.getEmail()).isEqualTo("updated@example.com");
        verify(userMapper).updateFromDto(dto, user);
        verify(userRepository).save(user);
    }

    @Test
    @DisplayName("Fail update user when email already exists")
    void shouldFailUpdateUserWhenEmailExists() {
        UUID userId = user.getUserId();

        UserUpdateRequestDto dto = new UserUpdateRequestDto();
        dto.setEmail("duplicate@example.com");

        when(userRepository.existsByEmailAndUserIdNot("duplicate@example.com", userId)).thenReturn(true);

        assertThatThrownBy(() -> userService.updateUser(userId, dto))
                .isInstanceOf(BadCredentialsException.class)
                .hasMessageContaining("Email already in use");
    }

    @Test
    @DisplayName("Fail update user when user not found")
    void shouldFailUpdateUserWhenUserNotFound() {
        UUID userId = UUID.randomUUID();

        UserUpdateRequestDto dto = new UserUpdateRequestDto();
        dto.setEmail("test@example.com");

        when(userRepository.existsByEmailAndUserIdNot("test@example.com", userId)).thenReturn(false);
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.updateUser(userId, dto))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("User not found");
    }
}