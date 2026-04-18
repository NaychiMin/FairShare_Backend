package com.example.fairsharebackend.mapper;

import com.example.fairsharebackend.entity.User;
import com.example.fairsharebackend.entity.dto.request.UserRegisterRequestDto;
import com.example.fairsharebackend.entity.dto.request.UserUpdateRequestDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import static org.assertj.core.api.Assertions.assertThat;

class UserMapperTest {

    private final UserMapper userMapper = Mappers.getMapper(UserMapper.class);

    @Test
    @DisplayName("Map UserRegisterRequestDto to User correctly")
    void shouldMapUserRegisterRequestDtoToEntity() {
        UserRegisterRequestDto dto = new UserRegisterRequestDto();
        dto.setName("Alice Tan");
        dto.setEmail("alice@example.com");

        User result = userMapper.toEntity(dto);

        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo("Alice Tan");
        assertThat(result.getEmail()).isEqualTo("alice@example.com");
    }

    @Test
    @DisplayName("Update existing User from UserUpdateRequestDto")
    void shouldUpdateExistingUserFromDto() {
        User user = new User();
        user.setName("Old Name");
        user.setEmail("old@example.com");

        UserUpdateRequestDto dto = new UserUpdateRequestDto();
        dto.setName("New Name");
        dto.setEmail("new@example.com");

        userMapper.updateFromDto(dto, user);

        assertThat(user.getName()).isEqualTo("New Name");
        assertThat(user.getEmail()).isEqualTo("new@example.com");
    }

    @Test
    @DisplayName("Update existing User with partial fields")
    void shouldUpdateExistingUserWithPartialFields() {
        User user = new User();
        user.setName("Original Name");
        user.setEmail("original@example.com");

        UserUpdateRequestDto dto = new UserUpdateRequestDto();
        dto.setName("Updated Name");
        dto.setEmail(null);

        userMapper.updateFromDto(dto, user);

        assertThat(user.getName()).isEqualTo("Updated Name");
    }
}