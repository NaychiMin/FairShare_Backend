package com.example.fairsharebackend.entity.dto.response;

import com.example.fairsharebackend.entity.User;

public class UserRegisterResponseDto {
    private User user;

    public UserRegisterResponseDto() {
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
