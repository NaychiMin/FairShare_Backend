package com.example.fairsharebackend.service;

import com.example.fairsharebackend.entity.UserBadge;

import java.util.List;
import java.util.UUID;

public interface BadgeNotificationService {
    void notifyBadgeEarned(UUID userId, UserBadge badge);
    void notifyBadgeEarnedToGroup(UUID groupId, UserBadge badge);
    void notifyMultipleBadgesEarned(UUID userId, List<UserBadge> badges);
}
