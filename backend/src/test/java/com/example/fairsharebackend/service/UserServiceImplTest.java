package com.example.fairsharebackend.service;

import com.example.fairsharebackend.entity.User;
import com.example.fairsharebackend.entity.UserCredential;
import com.example.fairsharebackend.entity.dto.request.UserLoginRequestDto;
import com.example.fairsharebackend.entity.dto.request.UserRegisterRequestDto;
import com.example.fairsharebackend.entity.dto.request.UserUpdatePasswordRequestDto;
import com.example.fairsharebackend.entity.dto.response.UserLoginResponseDto;
import com.example.fairsharebackend.mapper.UserMapper;
import com.example.fairsharebackend.repository.UserRepository;
import com.example.fairsharebackend.security.JwtUtil;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class UserServiceImplTest {
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

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetUserByEmail_UserExists() {
        User user = new User();
        user.setEmail("test@example.com");

        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));

        User result = userService.getUserByEmail("test@example.com");

        assertNotNull(result);
        assertEquals("test@example.com", result.getEmail());
    }

    @Test
    void testGetUserByEmail_UserNotFound() {
        when(userRepository.findByEmail("missing@example.com")).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
                () -> userService.getUserByEmail("missing@example.com"));

        assertEquals("User not found with email: missing@example.com", exception.getMessage());
    }

    @Test
    void testLogin_Success() {
        HttpServletResponse servletRes = mock(HttpServletResponse.class);
        UserLoginRequestDto request = new UserLoginRequestDto();
        request.setEmail("test@example.com");
        request.setPassword("password");

        User user = new User();
        user.setEmail("test@example.com");

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(mock(UsernamePasswordAuthenticationToken.class));
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));
        when(jwtUtil.generateToken(user)).thenReturn("jwt-token");

        UserLoginResponseDto response = userService.login(request, servletRes);

        assertNotNull(response);
        assertEquals(user, response.getUser());
        assertEquals("jwt-token", response.getJwt());
    }

    @Test
    void testLogin_BadCredentials() {
        HttpServletResponse servletRes = mock(HttpServletResponse.class);
        UserLoginRequestDto request = new UserLoginRequestDto();
        request.setEmail("test@example.com");
        request.setPassword("wrong");

        when(authenticationManager.authenticate(any())).thenThrow(new BadCredentialsException("Bad creds"));

        BadCredentialsException ex = assertThrows(BadCredentialsException.class,
                () -> userService.login(request, servletRes));

        assertEquals("Invalid credentials.", ex.getMessage());
    }

    @Test
    void testRegisterUser_Success() {
        UserRegisterRequestDto dto = new UserRegisterRequestDto();
        dto.setEmail("new@example.com");
        dto.setPassword("password");

        User userEntity = new User();
        when(userRepository.existsByEmail("new@example.com")).thenReturn(false);
        when(userMapper.toEntity(dto)).thenReturn(userEntity);
        when(passwordEncoder.encode("password")).thenReturn("hashed-password");
        when(userRepository.save(userEntity)).thenReturn(userEntity);

        User savedUser = userService.registerUser(dto);

        assertNotNull(savedUser);
        verify(userRepository).save(userEntity);
        assertEquals("hashed-password", userEntity.getUserCredential().getPasswordHash());
    }

    @Test
    void testUpdatePassword_Success() {
        UUID userId = UUID.randomUUID();
        UserUpdatePasswordRequestDto dto = new UserUpdatePasswordRequestDto();
        dto.setOldPassword("old");
        dto.setPassword("new");

        UserCredential credential = new UserCredential();
        credential.setPasswordHash("hashed-old");
        User user = new User();
        user.setUserCredential(credential);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("old", "hashed-old")).thenReturn(true);
        when(passwordEncoder.matches("new", "hashed-old")).thenReturn(false);
        when(passwordEncoder.encode("new")).thenReturn("hashed-new");

        userService.updatePassword(userId, dto);

        assertEquals("hashed-new", user.getUserCredential().getPasswordHash());
        verify(userRepository).save(user);
    }

    @Test
    void testUpdatePassword_WrongOldPassword() {
        UUID userId = UUID.randomUUID();
        UserUpdatePasswordRequestDto dto = new UserUpdatePasswordRequestDto();
        dto.setOldPassword("wrong");
        dto.setPassword("new");

        UserCredential credential = new UserCredential();
        credential.setPasswordHash("hashed-old");
        User user = new User();
        user.setUserCredential(credential);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("wrong", "hashed-old")).thenReturn(false);

        BadCredentialsException ex = assertThrows(BadCredentialsException.class,
                () -> userService.updatePassword(userId, dto));

        assertEquals("Old password is incorrect.", ex.getMessage());
    }
}