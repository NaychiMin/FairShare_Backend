package com.example.fairsharebackend.entity.dto.response;

import java.util.UUID;

public class UserSummaryResponseDto {
    private UUID userId;
    private String name;
    private String email;
    private String role;

    // Default constructor
    public UserSummaryResponseDto() {
    }

    // Getters and setters
    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}