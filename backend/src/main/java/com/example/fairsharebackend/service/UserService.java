package com.example.fairsharebackend.service;

import com.example.fairsharebackend.entity.User;
import com.example.fairsharebackend.entity.dto.request.UserLoginRequestDto;
import com.example.fairsharebackend.entity.dto.request.UserRegisterRequestDto;
import com.example.fairsharebackend.entity.dto.response.UserLoginResponseDto;

public interface UserService {
    User getUserByEmail(String email);
    UserLoginResponseDto login(UserLoginRequestDto request);
    User registerUser(UserRegisterRequestDto dto);
}
