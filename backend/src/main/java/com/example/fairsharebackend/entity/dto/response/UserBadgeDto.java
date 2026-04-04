package com.example.fairsharebackend.entity.dto.response;

import com.example.fairsharebackend.entity.Badge;
import com.example.fairsharebackend.entity.Group;
import com.example.fairsharebackend.entity.User;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.UUID;

public class UserBadgeDto {
    private UUID userBadgeId;
    private UserDto user;
    private Badge badge;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private GroupSummaryResponseDto group;

    public UserBadgeDto() {
    }

    public UUID getUserBadgeId() {
        return userBadgeId;
    }

    public void setUserBadgeId(UUID userBadgeId) {
        this.userBadgeId = userBadgeId;
    }

    public UserDto getUser() {
        return user;
    }

    public void setUser(UserDto user) {
        this.user = user;
    }

    public Badge getBadge() {
        return badge;
    }

    public void setBadge(Badge badge) {
        this.badge = badge;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public GroupSummaryResponseDto getGroup() {
        return group;
    }

    public void setGroup(GroupSummaryResponseDto group) {
        this.group = group;
    }
}
