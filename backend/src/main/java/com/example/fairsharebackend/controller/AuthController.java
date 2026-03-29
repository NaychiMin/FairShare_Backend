package com.example.fairsharebackend.controller;

import com.example.fairsharebackend.entity.User;
import com.example.fairsharebackend.entity.dto.request.UserLoginRequestDto;
import com.example.fairsharebackend.entity.dto.request.UserRegisterRequestDto;
import com.example.fairsharebackend.entity.dto.response.UserDto;
import com.example.fairsharebackend.entity.dto.response.UserLoginResponseDto;
import com.example.fairsharebackend.entity.dto.response.UserRegisterResponseDto;
import com.example.fairsharebackend.service.UserService;
import com.example.fairsharebackend.service.UserServiceImpl;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.*;

/*
 * No JWT needed to access these endpoints
 */
@RestController
@RequestMapping("/auth")
public class AuthController {
    private static final Logger log = LoggerFactory.getLogger(AuthController.class);

    private final UserService userService;
    public AuthController(
            UserService userService
    ) {
        this.userService = userService;
    }

    @GetMapping
    public ResponseEntity<?> test() {
        return ResponseEntity.ok("In auth!");
    }

    @PostMapping("/register")
    public ResponseEntity<UserRegisterResponseDto> register(
            @Valid @RequestBody UserRegisterRequestDto requestDto
            ) {
        UserRegisterResponseDto res = new UserRegisterResponseDto();
        User user = this.userService.registerUser(requestDto);
        res.setUser(user);
        return ResponseEntity.ok(res);
    }

    @PostMapping("/login")
    public ResponseEntity<UserLoginResponseDto> register(
            @Valid @RequestBody UserLoginRequestDto requestDto,
            HttpServletResponse response
    ) {
        UserLoginResponseDto res = this.userService.login(requestDto, response);
        return ResponseEntity.ok(res);
    }

    @GetMapping("/current")
    public ResponseEntity<UserDto> getCurrentUser(@CookieValue(name = "accessToken", required = false) String token) {
        log.info("In getCurrentUser");
        log.info("Token is {}", token);

        if (token == null || token.isBlank()) {
            return ResponseEntity.ok(null);
        }

        UserDto res = this.userService.getCurrentUser(token);
        return ResponseEntity.ok(res);
    }
}
