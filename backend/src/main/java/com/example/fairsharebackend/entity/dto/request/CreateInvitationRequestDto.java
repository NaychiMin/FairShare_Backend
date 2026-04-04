package com.example.fairsharebackend.entity.dto.request;

import jakarta.validation.constraints.Email;

public class CreateInvitationRequestDto {

    @Email(message = "Invalid email format")
    private String invitedEmail; // optional, null means link invite

    public CreateInvitationRequestDto() {}

    public String getInvitedEmail() {
        return invitedEmail;
    }

    public void setInvitedEmail(String invitedEmail) {
        this.invitedEmail = invitedEmail;
    }
}