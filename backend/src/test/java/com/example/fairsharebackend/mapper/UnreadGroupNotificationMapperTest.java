package com.example.fairsharebackend.dto;

import com.example.fairsharebackend.entity.dto.response.UnreadGroupNotificationDto;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class UnreadGroupNotificationDtoTest {

    @Test
    void shouldCreateAndModifyDto() {
        UUID groupId = UUID.randomUUID();

        UnreadGroupNotificationDto dto =
                new UnreadGroupNotificationDto(groupId, 5);

        assertThat(dto.getGroupId()).isEqualTo(groupId);
        assertThat(dto.getUnreadCount()).isEqualTo(5);

        dto.setUnreadCount(10);
        assertThat(dto.getUnreadCount()).isEqualTo(10);
    }
}