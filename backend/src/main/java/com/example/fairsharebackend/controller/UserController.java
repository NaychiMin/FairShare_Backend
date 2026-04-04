package com.example.fairsharebackend.controller;

import com.example.fairsharebackend.entity.User;
import com.example.fairsharebackend.entity.dto.request.UserRegisterRequestDto;
import com.example.fairsharebackend.entity.dto.request.UserUpdatePasswordRequestDto;
import com.example.fairsharebackend.entity.dto.request.UserUpdateRequestDto;
import com.example.fairsharebackend.entity.dto.response.UserRegisterResponseDto;
import com.example.fairsharebackend.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/user")
public class UserController {
    private final UserService userService;
    public UserController(
            UserService userService
    ) {
        this.userService = userService;
    }

    @PutMapping("/{userId}")
    public ResponseEntity<UserRegisterResponseDto> updateUser(
            @PathVariable UUID userId,
            @Valid @RequestBody UserUpdateRequestDto dto
    ) {
        UserRegisterResponseDto res = new UserRegisterResponseDto();
        User updatedUser = userService.updateUser(userId, dto);
        res.setUser(updatedUser);
        return ResponseEntity.ok(res);
    }

    @PutMapping("/{userId}/update-password")
    public ResponseEntity<String> updatePassword(
            @PathVariable UUID userId,
            @Valid @RequestBody UserUpdatePasswordRequestDto dto
    ) {
        userService.updatePassword(userId, dto);
        return ResponseEntity.ok("Password updated successfully.");
    }
}