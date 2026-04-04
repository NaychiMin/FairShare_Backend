package com.example.fairsharebackend.mapper;

import com.example.fairsharebackend.entity.Group;
import com.example.fairsharebackend.entity.dto.request.GroupCreateRequestDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import static org.assertj.core.api.Assertions.assertThat;

class GroupMapperTest {

    private final GroupMapper groupMapper = Mappers.getMapper(GroupMapper.class);

    @Test
    @DisplayName("Map GroupCreateRequestDto to Group correctly")
    void shouldMapGroupCreateRequestDtoToEntity() {
        GroupCreateRequestDto dto = new GroupCreateRequestDto();
        dto.setGroupName("Weekend Trip");
        dto.setCategory("Travel");
        dto.setAdmin("admin@example.com");

        Group result = groupMapper.toEntity(dto);

        assertThat(result).isNotNull();
        assertThat(result.getGroupName()).isEqualTo("Weekend Trip");
        assertThat(result.getCategory()).isEqualTo("Travel");
    }

    @Test
    @DisplayName("Map GroupCreateRequestDto with null optional fields")
    void shouldMapGroupCreateRequestDtoWithNullFields() {
        GroupCreateRequestDto dto = new GroupCreateRequestDto();
        dto.setGroupName("Test Group");

        Group result = groupMapper.toEntity(dto);

        assertThat(result).isNotNull();
        assertThat(result.getGroupName()).isEqualTo("Test Group");
    }
}