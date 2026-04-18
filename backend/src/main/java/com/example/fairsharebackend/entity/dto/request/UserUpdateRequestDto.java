package com.example.fairsharebackend.entity.dto.request;

public class UserUpdateRequestDto {
    private String name;
    private String email;

    public UserUpdateRequestDto() {
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
}
