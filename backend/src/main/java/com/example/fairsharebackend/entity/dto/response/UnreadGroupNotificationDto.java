package com.example.fairsharebackend.entity.dto.response;

import java.util.UUID;

public class UnreadGroupNotificationDto {
    private UUID groupId;
    private long unreadCount;

    public UnreadGroupNotificationDto(UUID groupId, long unreadCount) {
        this.groupId = groupId;
        this.unreadCount = unreadCount;
    }

    public UUID getGroupId() {
        return groupId;
    }

    public void setGroupId(UUID groupId) {
        this.groupId = groupId;
    }

    public long getUnreadCount() {
        return unreadCount;
    }

    public void setUnreadCount(long unreadCount) {
        this.unreadCount = unreadCount;
    }
}