package com.example.fairsharebackend.dto;

import com.example.fairsharebackend.entity.Badge;
import com.example.fairsharebackend.entity.dto.response.GroupSummaryResponseDto;
import com.example.fairsharebackend.entity.dto.response.UserBadgeDto;
import com.example.fairsharebackend.entity.dto.response.UserDto;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class UserBadgeDtoTest {

    @Test
    void shouldSetAndGetFields() {
        UserBadgeDto dto = new UserBadgeDto();

        UUID id = UUID.randomUUID();
        LocalDateTime now = LocalDateTime.now();

        dto.setUserBadgeId(id);
        dto.setUser(new UserDto());
        dto.setBadge(new Badge());
        dto.setCreatedAt(now);
        dto.setUpdatedAt(now);
        dto.setGroup(new GroupSummaryResponseDto(id, "Group", "Cat", true, "ACTIVE"));

        assertThat(dto.getUserBadgeId()).isEqualTo(id);
        assertThat(dto.getCreatedAt()).isEqualTo(now);
        assertThat(dto.getUpdatedAt()).isEqualTo(now);
        assertThat(dto.getGroup()).isNotNull();
    }
}