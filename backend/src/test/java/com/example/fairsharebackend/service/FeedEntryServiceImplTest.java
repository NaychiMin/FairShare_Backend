package com.example.fairsharebackend.service;

import com.example.fairsharebackend.entity.FeedEntry;
import com.example.fairsharebackend.entity.dto.request.FeedEntryFilterRequestDto;
import com.example.fairsharebackend.entity.dto.response.FeedEntryResponseDto;
import com.example.fairsharebackend.mapper.FeedEntryMapper;
import com.example.fairsharebackend.repository.FeedEntryRepository;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.data.domain.*;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FeedEntryServiceImplTest {

    @Mock private FeedEntryRepository repository;
    @Mock private FeedEntryMapper mapper;

    @InjectMocks
    private FeedEntryServiceImpl service;

    @Test
    @DisplayName("Return paginated feed entries successfully")
    void shouldReturnPagedResults() {
        // ARRANGE
        FeedEntryFilterRequestDto dto = new FeedEntryFilterRequestDto();
        dto.setPage(0);
        dto.setSize(10);
        dto.setDirection("DESC");
        dto.setSortBy("createdDate");

        FeedEntry entity = new FeedEntry();
        FeedEntryResponseDto responseDto = new FeedEntryResponseDto();

        Page<FeedEntry> page = new PageImpl<>(List.of(entity));

        when(repository.findFeedEntryFilteredSorted(any(), any(), any(), any()))
                .thenReturn(page);
        when(mapper.toDto(entity)).thenReturn(responseDto);

        // ACT
        Page<FeedEntryResponseDto> result = service.findFeedEntryFilteredSorted(dto);

        // ASSERT
        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0)).isEqualTo(responseDto);

        verify(repository, times(1))
                .findFeedEntryFilteredSorted(any(), any(), any(), any());
        verify(mapper, times(1)).toDto(entity);
    }
}