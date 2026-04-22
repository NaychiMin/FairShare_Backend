package com.example.fairsharebackend.dto;

import com.example.fairsharebackend.entity.dto.response.CreateInvitationResponseDto;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class CreateInvitationResponseDtoTest {

    @Test
    void shouldCreateDtoCorrectly() {
        CreateInvitationResponseDto dto =
                new CreateInvitationResponseDto(
                        "token123",
                        "http://invite.link",
                        "test@example.com",
                        "PENDING"
                );

        assertThat(dto.getToken()).isEqualTo("token123");
        assertThat(dto.getInviteLink()).isEqualTo("http://invite.link");
        assertThat(dto.getInvitedEmail()).isEqualTo("test@example.com");
        assertThat(dto.getStatus()).isEqualTo("PENDING");
    }
}