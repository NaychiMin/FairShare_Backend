package com.example.fairsharebackend.dto;

import com.example.fairsharebackend.entity.dto.response.UserUpdatedResponseDto;
import com.example.fairsharebackend.entity.dto.response.UserDto;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class UserUpdatedResponseDtoTest {

    @Test
    void shouldSetAndGetUser() {
        UserUpdatedResponseDto dto = new UserUpdatedResponseDto();
        UserDto user = new UserDto();

        dto.setUser(user);

        assertThat(dto.getUser()).isEqualTo(user);
    }
}