package com.example.fairsharebackend.repository;

import java.util.UUID;

public interface UnreadGroupNotificationProjection {
    UUID getGroupId();
    long getUnreadCount();
}