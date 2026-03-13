package com.example.fairsharebackend.entity.dto.response;

import java.util.UUID;

public class GroupSummaryResponseDto {
    private UUID groupId;
    private String groupName;
    private String category;
    private boolean isAdmin;
    private String status;

    public GroupSummaryResponseDto(UUID groupId, String groupName, String category, boolean isAdmin, String status) {
        this.groupId = groupId;
        this.groupName = groupName;
        this.category = category;
        this.isAdmin = isAdmin;
        this.status = status;
    }

    public UUID getGroupId() { return groupId; }
    public String getGroupName() { return groupName; }
    public String getCategory() { return category; }
    public boolean getIsAdmin() { return isAdmin; } // ensures JSON key "isAdmin"
    public String getStatus() { return status; }
}