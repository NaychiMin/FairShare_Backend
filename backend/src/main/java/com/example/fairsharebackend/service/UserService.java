package com.example.fairsharebackend.service;

import com.example.fairsharebackend.entity.User;
import com.example.fairsharebackend.entity.dto.request.UserLoginRequestDto;
import com.example.fairsharebackend.entity.dto.request.UserRegisterRequestDto;
import com.example.fairsharebackend.entity.dto.request.UserUpdateRequestDto;
import com.example.fairsharebackend.entity.dto.response.UserLoginResponseDto;

import java.util.UUID;

public interface UserService {
    User getUserByEmail(String email);
    UserLoginResponseDto login(UserLoginRequestDto request);
    User registerUser(UserRegisterRequestDto dto);
    User updateUser(UUID userId, UserUpdateRequestDto dto);
}
