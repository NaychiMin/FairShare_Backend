package com.example.fairsharebackend.service;

import com.example.fairsharebackend.entity.FeedEntry;
import com.example.fairsharebackend.entity.dto.request.FeedEntryFilterRequestDto;
import com.example.fairsharebackend.entity.dto.response.FeedEntryResponseDto;
import org.springframework.data.domain.Page;

public interface FeedEntryService {
    Page<FeedEntryResponseDto> findFeedEntryFilteredSorted(FeedEntryFilterRequestDto dto);
}
