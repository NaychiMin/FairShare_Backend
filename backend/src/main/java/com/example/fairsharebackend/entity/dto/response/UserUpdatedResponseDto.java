package com.example.fairsharebackend.entity.dto.response;

import com.example.fairsharebackend.entity.User;

public class UserUpdatedResponseDto {
    private UserDto user;

    public UserUpdatedResponseDto() {
    }

    public UserDto getUser() {
        return user;
    }

    public void setUser(UserDto user) {
        this.user = user;
    }
}
