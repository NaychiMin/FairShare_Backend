package com.example.fairsharebackend.entity.dto.response;

public class GroupInvitationDetailResponseDto {
    private String token;
    private String groupName;
    private String category;
    private String invitedEmail;
    private String status;
    private String expiresAt;
    private String createdAt;
    private String createdByEmail;

    public GroupInvitationDetailResponseDto(
            String token,
            String groupName,
            String category,
            String invitedEmail,
            String status,
            String expiresAt,
            String createdAt,
            String createdByEmail
    ) {
        this.token = token;
        this.groupName = groupName;
        this.category = category;
        this.invitedEmail = invitedEmail;
        this.status = status;
        this.expiresAt = expiresAt;
        this.createdAt = createdAt;
        this.createdByEmail = createdByEmail;
    }

    public String getToken() {
        return token;
    }

    public String getGroupName() {
        return groupName;
    }

    public String getCategory() {
        return category;
    }

    public String getInvitedEmail() {
        return invitedEmail;
    }

    public String getStatus() {
        return status;
    }

    public String getExpiresAt() {
        return expiresAt;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public String getCreatedByEmail() {
        return createdByEmail;
    }
}