package com.example.fairsharebackend.service;

import com.example.fairsharebackend.entity.dto.response.UserBadgeResponseDto;
import java.util.List;

public interface UserBadgeService {
    List<UserBadgeResponseDto> getUserBadges(String email);
}