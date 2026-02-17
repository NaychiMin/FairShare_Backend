package com.example.fairsharebackend.controller;

import com.example.fairsharebackend.entity.User;
import com.example.fairsharebackend.entity.dto.request.UserLoginRequestDto;
import com.example.fairsharebackend.entity.dto.request.UserRegisterRequestDto;
import com.example.fairsharebackend.entity.dto.response.UserLoginResponseDto;
import com.example.fairsharebackend.entity.dto.response.UserRegisterResponseDto;
import com.example.fairsharebackend.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/*
 * No JWT needed to access these endpoints
 */
@RestController
@RequestMapping("/auth")
public class AuthController {
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
            @RequestBody UserRegisterRequestDto requestDto
            ) {
        UserRegisterResponseDto res = new UserRegisterResponseDto();
        User user = this.userService.registerUser(requestDto);
        res.setUser(user);
        return ResponseEntity.ok(res);
    }

    @PostMapping("/login")
    public ResponseEntity<UserLoginResponseDto> register(
            @RequestBody UserLoginRequestDto requestDto
    ) {
        UserLoginResponseDto res = this.userService.login(requestDto);
        return ResponseEntity.ok(res);
    }
}
