package com.example.fairsharebackend.entity.dto.response;

import java.time.LocalDateTime;
import java.util.List;

public class UserBadgeResponseDto {
    private String badgeName;
    private String description;
    private String badgeType;
    private String badgeScope;
    private String groupName; // nullable if personal badge
    private LocalDateTime earnedAt;

    public UserBadgeResponseDto() {
    }

    public String getBadgeName() {
        return badgeName;
    }

    public void setBadgeName(String badgeName) {
        this.badgeName = badgeName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getBadgeType() {
        return badgeType;
    }

    public void setBadgeType(String badgeType) {
        this.badgeType = badgeType;
    }

    public String getBadgeScope() {
        return badgeScope;
    }

    public void setBadgeScope(String badgeScope) {
        this.badgeScope = badgeScope;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public LocalDateTime getEarnedAt() {
        return earnedAt;
    }

    public void setEarnedAt(LocalDateTime earnedAt) {
        this.earnedAt = earnedAt;
    }

}