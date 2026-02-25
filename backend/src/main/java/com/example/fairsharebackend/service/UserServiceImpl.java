package com.example.fairsharebackend.service;

import com.example.fairsharebackend.entity.User;
import com.example.fairsharebackend.entity.UserCredential;
import com.example.fairsharebackend.entity.dto.request.UserLoginRequestDto;
import com.example.fairsharebackend.entity.dto.request.UserRegisterRequestDto;
import com.example.fairsharebackend.entity.dto.request.UserUpdatePasswordRequestDto;
import com.example.fairsharebackend.entity.dto.request.UserUpdateRequestDto;
import com.example.fairsharebackend.entity.dto.response.UserLoginResponseDto;
import com.example.fairsharebackend.mapper.UserMapper;
import com.example.fairsharebackend.repository.UserRepository;
import com.example.fairsharebackend.security.JwtUtil;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.UUID;

@Service
@Transactional
public class UserServiceImpl implements UserService {
    private static final Logger log = LoggerFactory.getLogger(UserServiceImpl.class);

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;

    public UserServiceImpl(
            UserRepository userRepository,
            UserMapper userMapper,
            AuthenticationManager authenticationManager,
            JwtUtil jwtUtil,
            PasswordEncoder passwordEncoder
    ) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public User getUserByEmail(String email) {
        log.info("Finding User by email:: {}", email);
        return userRepository.findByEmail(email).orElseThrow(() -> new EntityNotFoundException("User not found with email: " + email));
    }

    @Override
    @Transactional
    public UserLoginResponseDto login(UserLoginRequestDto request) {
        log.error("Logging in with email :: {}", request.getEmail());
        try {
            String email = this.normaliseEmail(request.getEmail());

            Authentication auth = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(email, request.getPassword()) // Spring security automatically checks raw password against hashed password
            );
            UserLoginResponseDto res = new UserLoginResponseDto();
            User user = this.getUserByEmail(email);
            String jwt = jwtUtil.generateToken(user);
            res.setUser(user);
            res.setJwt(jwt);
            return res;
        } catch (BadCredentialsException e) {
            log.error("Exception :: {}", e.getMessage());
            throw new BadCredentialsException("Invalid credentials.");
        } catch (Exception e) {
            log.error("Exception :: {}", e.getMessage());
            throw new RuntimeException("Unable to log in at this time. Please try again later.");
        }
    }

    @Override
    @Transactional
    public User registerUser(UserRegisterRequestDto dto) {
        log.error("Registering with email :: {}", dto.getEmail());
        try {
            this.validatedRegisterRequestDto(dto);
            dto.setEmail(this.normaliseEmail(dto.getEmail()));

            User user = this.userMapper.toEntity(dto);

            UserCredential cred = new UserCredential();
            cred.setPasswordHash(passwordEncoder.encode(dto.getPassword())); // hash password
            cred.setUser(user);

            user.setUserCredential(cred);
            return userRepository.save(user);
        } catch (BadCredentialsException e) {
            log.error("Exception :: {}", e.getMessage());

            throw e;
        } catch (Exception e) {
            log.error("Exception :: {}", e.getMessage());

            throw new RuntimeException("Unable to register at this time. Please try again later.");
        }
    }

    @Override
    @Transactional
    public User updateUser(UUID userId, UserUpdateRequestDto dto) {
        log.info("Updating user details for userId :: {}", userId);

        try {
            this.validatedUpdateRequestDto(userId, dto);

            User user = userRepository.findById(userId)
                    .orElseThrow(() ->
                            new EntityNotFoundException("User not found with ID: " + userId)
                    );

            if (!dto.getEmail().isBlank() && !dto.getEmail().isEmpty()) {
                String email = this.normaliseEmail(dto.getEmail());
                dto.setEmail(email);
            }

            userMapper.updateFromDto(dto, user);
            return userRepository.save(user);
        } catch (BadCredentialsException | EntityNotFoundException e) {
            log.error("Exception :: {}", e.getMessage());
            throw e;

        } catch (Exception e) {
            log.error("Exception :: {}", e.getMessage());
            throw new RuntimeException("Unable to update user at this time.");
        }
    }

    private void validatedRegisterRequestDto(UserRegisterRequestDto dto) {
        if (userRepository.existsByEmail(dto.getEmail())) {
            throw new BadCredentialsException("Email already in use.");
        }
    }

    private void validatedUpdateRequestDto(UUID userId, UserUpdateRequestDto dto) {
        if (userRepository.existsByEmailAndUserIdNot(dto.getEmail(), userId)) {
            throw new BadCredentialsException("Email already in use.");
        }
    }

    private String normaliseEmail(String email) {
        return email.trim().toLowerCase();
    }

    @Override
    @Transactional
    public void updatePassword(UUID userId, UserUpdatePasswordRequestDto dto) {
        log.info("Updating password for userId :: {}", userId);

        try {
            User user = userRepository.findById(userId)
                    .orElseThrow(() ->
                            new EntityNotFoundException("User not found with ID: " + userId)
                    );

            UserCredential credential = user.getUserCredential();
            if (credential == null) {
                throw new RuntimeException("User cannot be validated at this time. Please try again later");
            }

            // Verify old password
            boolean matches = passwordEncoder.matches(dto.getOldPassword(), credential.getPasswordHash());
            if (!matches) {
                throw new BadCredentialsException("Old password is incorrect.");
            }

            // Prevent same password reuse
            if (passwordEncoder.matches(dto.getPassword(), credential.getPasswordHash())) {
                throw new BadCredentialsException("New password cannot be the same as the old password.");
            }

            // Encode and update password
            credential.setPasswordHash(passwordEncoder.encode(dto.getPassword()));

            userRepository.save(user);

        } catch (BadCredentialsException | EntityNotFoundException e) {
            log.error("Exception :: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Exception :: {}", e.getMessage());
            throw new RuntimeException("Unable to update password at this time.");
        }
    }
}
