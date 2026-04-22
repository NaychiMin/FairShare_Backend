package com.example.fairsharebackend.dto;

import com.example.fairsharebackend.entity.dto.response.FeedEntryResponseDto;
import com.example.fairsharebackend.constant.FeedEntryType;
import com.example.fairsharebackend.constant.GroupField;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class FeedEntryResponseDtoTest {

    @Test
    void shouldSetAndGetFieldsCorrectly() {
        FeedEntryResponseDto dto = new FeedEntryResponseDto();

        UUID id = UUID.randomUUID();
        LocalDateTime now = LocalDateTime.now();

        dto.setFeedEntryId(id);
        dto.setFeedEntryType(FeedEntryType.EXPENSE_ADDED);
        dto.setGroupUpdatedField(GroupField.GROUP_NAME);
        dto.setGroupUpdatedFieldOld("Old");
        dto.setGroupUpdatedFieldNew("New");
        dto.setCreatedDate(now);

        assertThat(dto.getFeedEntryId()).isEqualTo(id);
        assertThat(dto.getFeedEntryType()).isEqualTo(FeedEntryType.EXPENSE_ADDED);
        assertThat(dto.getGroupUpdatedField()).isEqualTo(GroupField.GROUP_NAME);
        assertThat(dto.getGroupUpdatedFieldOld()).isEqualTo("Old");
        assertThat(dto.getGroupUpdatedFieldNew()).isEqualTo("New");
        assertThat(dto.getCreatedDate()).isEqualTo(now);
    }
}