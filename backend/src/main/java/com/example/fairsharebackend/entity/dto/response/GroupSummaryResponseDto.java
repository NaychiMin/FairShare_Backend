package com.example.fairsharebackend.entity.dto.response;

import java.util.UUID;

public class GroupSummaryResponseDto {
    private UUID groupId;
    private String groupName;
    private String category;
    private boolean isAdmin;

    public GroupSummaryResponseDto(UUID groupId, String groupName, String category, boolean isAdmin) {
        this.groupId = groupId;
        this.groupName = groupName;
        this.category = category;
        this.isAdmin = isAdmin;
    }

    public UUID getGroupId() { return groupId; }
    public String getGroupName() { return groupName; }
    public String getCategory() { return category; }
//    public boolean isAdmin() { return isAdmin; }

    public boolean getIsAdmin() { return isAdmin; }
}