package com.example.fairsharebackend.service;

import com.example.fairsharebackend.entity.User;
import com.example.fairsharebackend.entity.dto.request.UserRegisterRequestDto;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

public interface UserAuthService {
    User registerUser(UserRegisterRequestDto dto);
}
