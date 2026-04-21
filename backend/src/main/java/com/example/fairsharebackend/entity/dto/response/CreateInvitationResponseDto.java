package com.example.fairsharebackend.entity.dto.response;

public class CreateInvitationResponseDto {
    private String token;
    private String inviteLink;
    private String invitedEmail;
    private String status;

    public CreateInvitationResponseDto(String token, String inviteLink, String invitedEmail, String status) {
        this.token = token;
        this.inviteLink = inviteLink;
        this.invitedEmail = invitedEmail;
        this.status = status;
    }

    public String getToken() {
        return token;
    }

    public String getInviteLink() {
        return inviteLink;
    }

    public String getInvitedEmail() {
        return invitedEmail;
    }

    public String getStatus() {
        return status;
    }
}