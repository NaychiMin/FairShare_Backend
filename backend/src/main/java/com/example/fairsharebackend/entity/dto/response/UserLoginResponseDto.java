package com.example.fairsharebackend.entity.dto.response;

import com.example.fairsharebackend.entity.User;

public class UserLoginResponseDto {
    private UserDto user;
    private String jwt;

    public UserLoginResponseDto() {
    }

    public UserDto getUser() {
        return user;
    }

    public void setUser(UserDto user) {
        this.user = user;
    }

    public String getJwt() {
        return jwt;
    }

    public void setJwt(String jwt) {
        this.jwt = jwt;
    }
}
