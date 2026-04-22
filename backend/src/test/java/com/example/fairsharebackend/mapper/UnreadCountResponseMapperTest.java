package com.example.fairsharebackend.dto;

import com.example.fairsharebackend.entity.dto.response.UnreadCountResponseDto;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class UnreadCountResponseDtoTest {

    @Test
    void shouldCreateAndModifyDto() {
        UnreadCountResponseDto dto = new UnreadCountResponseDto(3);

        assertThat(dto.getUnreadCount()).isEqualTo(3);

        dto.setUnreadCount(7);
        assertThat(dto.getUnreadCount()).isEqualTo(7);
    }
}