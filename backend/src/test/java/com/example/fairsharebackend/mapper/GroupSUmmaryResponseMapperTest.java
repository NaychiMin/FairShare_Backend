package com.example.fairsharebackend.dto;

import com.example.fairsharebackend.entity.dto.response.GroupSummaryResponseDto;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class GroupSummaryResponseDtoTest {

    @Test
    void shouldCreateDtoCorrectly() {
        UUID groupId = UUID.randomUUID();

        GroupSummaryResponseDto dto =
                new GroupSummaryResponseDto(
                        groupId,
                        "Trip",
                        "Travel",
                        true,
                        "ACTIVE"
                );

        assertThat(dto.getGroupId()).isEqualTo(groupId);
        assertThat(dto.getGroupName()).isEqualTo("Trip");
        assertThat(dto.getCategory()).isEqualTo("Travel");
        assertThat(dto.getIsAdmin()).isTrue();
        assertThat(dto.getStatus()).isEqualTo("ACTIVE");
    }
}