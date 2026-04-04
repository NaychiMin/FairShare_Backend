package com.example.fairsharebackend.service;

import com.example.fairsharebackend.entity.dto.response.UserBadgeDto;

import java.util.List;
import java.util.UUID;

public interface BadgeNotificationService {
    void notifyBadgeEarned(UUID userId, UserBadgeDto badge);
    void notifyBadgeEarnedToGroup(UUID groupId, UserBadgeDto badge);
    void notifyMultipleBadgesEarned(UUID userId, List<UserBadgeDto> badges);
}
