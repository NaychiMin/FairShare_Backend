package com.example.fairsharebackend.service;

import com.example.fairsharebackend.entity.Group;
import com.example.fairsharebackend.entity.User;
import com.example.fairsharebackend.entity.dto.response.NotificationResponseDto;
import com.example.fairsharebackend.entity.dto.response.UnreadGroupNotificationDto;

import java.util.List;
import java.util.UUID;

public interface NotificationService {
    void notifyUser(User recipient, User actor, Group group, String type, String message, UUID referenceId);
    void notifyUsers(List<User> recipients, User actor, Group group, String type, String message, UUID referenceId);

    List<NotificationResponseDto> getMyNotifications(String email);
    long getUnreadCount(String email);
    List<UnreadGroupNotificationDto> getUnreadCountsByGroup(String email);

    void markAsRead(UUID notificationId, String email);
    void markAllAsRead(String email);
}
