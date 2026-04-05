package com.example.fairsharebackend.entity.dto.response;

import java.time.LocalDateTime;
import java.util.UUID;

public class NotificationResponseDto {

    private UUID notificationId;
    private String type;
    private String message;
    private boolean isRead;
    private LocalDateTime createdAt;
    private UUID groupId;
    private String actorName;



    public NotificationResponseDto(UUID notificationId, String type, String message, boolean isRead, LocalDateTime createdAt, UUID groupId, String actorName) {
        this.notificationId = notificationId;
        this.type = type;
        this.message = message;
        this.isRead = isRead;
        this.createdAt = createdAt;
        this.groupId = groupId;
        this.actorName = actorName;
    }

    public UUID getNotificationId() {
        return notificationId;
    }

    public void setNotificationId(UUID notificationId) {
        this.notificationId = notificationId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean isRead() {
        return isRead;
    }

    public void setRead(boolean read) {
        isRead = read;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public UUID getGroupId() {
        return groupId;
    }

    public void setGroupId(UUID groupId) {
        this.groupId = groupId;
    }

    public String getActorName() {
        return actorName;
    }

    public void setActorName(String actorName) {
        this.actorName = actorName;
    }
}
