package com.example.fairsharebackend.dto;

import com.example.fairsharebackend.entity.User;
import com.example.fairsharebackend.entity.dto.response.UserRegisterResponseDto;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class UserRegisterResponseDtoTest {

    @Test
    void shouldSetAndGetUser() {
        UserRegisterResponseDto dto = new UserRegisterResponseDto();
        User user = new User();

        dto.setUser(user);

        assertThat(dto.getUser()).isEqualTo(user);
    }
}