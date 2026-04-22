package com.example.fairsharebackend.dto;

import com.example.fairsharebackend.entity.dto.response.NotificationResponseDto;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class NotificationResponseDtoTest {

    @Test
    void shouldCreateAndModifyNotificationDto() {
        UUID id = UUID.randomUUID();
        UUID groupId = UUID.randomUUID();
        LocalDateTime now = LocalDateTime.now();

        NotificationResponseDto dto = new NotificationResponseDto(
                id, "INFO", "Test message", false, now, groupId, "Alice"
        );

        // constructor assertions
        assertThat(dto.getNotificationId()).isEqualTo(id);
        assertThat(dto.getType()).isEqualTo("INFO");
        assertThat(dto.getMessage()).isEqualTo("Test message");
        assertThat(dto.isRead()).isFalse();
        assertThat(dto.getCreatedAt()).isEqualTo(now);
        assertThat(dto.getGroupId()).isEqualTo(groupId);
        assertThat(dto.getActorName()).isEqualTo("Alice");

        // setter coverage (important for Sonar)
        dto.setRead(true);
        dto.setActorName("Bob");

        assertThat(dto.isRead()).isTrue();
        assertThat(dto.getActorName()).isEqualTo("Bob");
    }
}