package com.example.fairsharebackend.dto;

import com.example.fairsharebackend.entity.dto.request.FeedEntryFilterRequestDto;
import com.example.fairsharebackend.constant.FeedEntryType;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class FeedEntryFilterRequestDtoTest {

    @Test
    void shouldSetAndGetAllFieldsCorrectly() {
        FeedEntryFilterRequestDto dto = new FeedEntryFilterRequestDto();

        UUID userId = UUID.randomUUID();
        UUID groupId = UUID.randomUUID();

        dto.setUserId(userId);
        dto.setGroupId(groupId);
        dto.setPage(2);
        dto.setSize(20);
        dto.setSortBy("name");
        dto.setDirection("ASC");
        dto.setTypes(List.of(FeedEntryType.EXPENSE_ADDED));

        assertThat(dto.getUserId()).isEqualTo(userId);
        assertThat(dto.getGroupId()).isEqualTo(groupId);
        assertThat(dto.getPage()).isEqualTo(2);
        assertThat(dto.getSize()).isEqualTo(20);
        assertThat(dto.getSortBy()).isEqualTo("name");
        assertThat(dto.getDirection()).isEqualTo("ASC");
        assertThat(dto.getTypes()).containsExactly(FeedEntryType.EXPENSE_ADDED);
    }

    @Test
    void shouldHaveDefaultValues() {
        FeedEntryFilterRequestDto dto = new FeedEntryFilterRequestDto();

        assertThat(dto.getPage()).isEqualTo(0);
        assertThat(dto.getSize()).isEqualTo(10);
        assertThat(dto.getSortBy()).isEqualTo("createdDate");
        assertThat(dto.getDirection()).isEqualTo("DESC");
    }
}